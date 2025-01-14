package com.example.blooddonor.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.domain.models.LocationTypeDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.implementation.LocationTypeUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.LocationTypeUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.ui.adapters.LocationAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationListActivity extends BaseActivity {

    private LocationAdapter locationAdapter;
    private MaterialAutoCompleteTextView locationTypeDropdown;
    private TextInputEditText searchInput;
    private ActivityResultLauncher<Intent> editLocationLauncher;

    private LocationUseCase locationUseCase;
    private RoleUseCase roleUseCase;
    private LocationTypeUseCase locationTypeUseCase;
    private List<LocationDTO> allLocations;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_location_list, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_location_list);
        setTitle(R.string.locations);

        initializeDependencies();
        initializeCurrentUser();
        initializeActivityResultLauncher();
        initializeUIElements();
        loadLocations();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
        locationTypeUseCase = new LocationTypeUseCaseImpl(locationTypeRepository);
    }

    private void initializeUIElements() {
        RecyclerView recyclerView = findViewById(R.id.location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationAdapter = new LocationAdapter(new ArrayList<>(), new LocationAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(LocationDTO location) {
                showLocationDetails(location);
            }

            @Override
            public void onEditClick(LocationDTO location) {
                openEditLocationActivity(location);
            }

            @Override
            public void onDeleteClick(int locationId) {
                handleDeleteLocation(locationId);
            }
        });

        recyclerView.setAdapter(locationAdapter);

        locationTypeDropdown = findViewById(R.id.location_type_dropdown);
        searchInput = findViewById(R.id.search_input);

        setupLocationTypeDropdown();
        setupSearchInput();
    }

    private void setupLocationTypeDropdown() {
        List<LocationTypeDTO> locationTypes = locationTypeUseCase.getAllLocationTypes();

        LocationTypeDTO allType = new LocationTypeDTO();
        allType.setId(0);
        allType.setName(getString(R.string.all));
        locationTypes.add(0, allType);

        List<String> locationTypeNames = new ArrayList<>();
        for (LocationTypeDTO type : locationTypes) {
            locationTypeNames.add(type.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationTypeNames);
        locationTypeDropdown.setAdapter(adapter);

        locationTypeDropdown.setOnClickListener(v -> locationTypeDropdown.showDropDown());
        locationTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                locationTypeDropdown.showDropDown();
            }
        });

        locationTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            filterLocationsBySelectedType(locationTypes.get(position));
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterLocationsBySelectedType(LocationTypeDTO selectedType) {
        List<LocationDTO> filteredLocations = new ArrayList<>();

        for (LocationDTO location : allLocations) {
            if (selectedType.getId() == 0 || location.getLocationTypeId() == selectedType.getId()) {
                filteredLocations.add(location);
            }
        }

        locationAdapter.setLocations(filteredLocations);
        locationAdapter.notifyDataSetChanged();
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterBySearch() {
        String query = Objects.requireNonNull(searchInput.getText()).toString().toLowerCase();

        List<LocationDTO> filteredLocations = new ArrayList<>();
        for (LocationDTO location : allLocations) {
            if (location.getName().toLowerCase().contains(query)) {
                filteredLocations.add(location);
            }
        }

        locationAdapter.setLocations(filteredLocations);
        locationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadLocations() {
        allLocations = locationUseCase.getAllLocations();
        locationAdapter.setLocations(allLocations);
        locationAdapter.notifyDataSetChanged();
    }

    private void showLocationDetails(LocationDTO location) {
        String locationDetails = getString(R.string.location_details,
                location.getName(),
                location.getLocation(),
                location.getPhoneNumbers(),
                location.getLocationTypeName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.location_details_title)
                .setMessage(locationDetails)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void handleDeleteLocation(int locationId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_location_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (locationUseCase.deleteLocation(locationId)) {
                        showToast(getString(R.string.location_deleted));
                        loadLocations();
                    } else {
                        showToast(getString(R.string.error_deleting_location));
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void initializeActivityResultLauncher() {
        editLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadLocations();
                    }
                }
        );
    }

    private void openEditLocationActivity(LocationDTO location) {
        Intent intent = new Intent(this, EditLocationActivity.class);
        intent.putExtra("userDTO", currentUser);
        intent.putExtra("location", location);
        editLocationLauncher.launch(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}