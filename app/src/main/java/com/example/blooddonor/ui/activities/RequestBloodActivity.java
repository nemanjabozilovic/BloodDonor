package com.example.blooddonor.ui.activities;

import static com.example.blooddonor.utils.TextInputHelper.addTextWatcher;
import static com.example.blooddonor.utils.TextInputHelper.clearError;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.domain.models.LocationTypeDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.implementation.BloodRequestUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationTypeUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationTypeUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.ui.adapters.LocationAdapter;
import com.example.blooddonor.utils.BloodRequestHelper;
import com.example.blooddonor.utils.TextInputHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RequestBloodActivity extends BaseActivity {

    private TextInputEditText patientNameField, possibleDonorsField, deadlineField, locationField;
    private TextInputLayout patientNameInputLayout, deadlineInputLayout, locationInputLayout, bloodTypeInputLayout;
    private AutoCompleteTextView bloodTypeDropdown;
    private MaterialButton submitRequestButton;

    private BloodRequestUseCase bloodRequestUseCase;
    private LocationUseCase locationUseCase;
    private LocationTypeUseCase locationTypeUseCase;
    private RoleUseCase roleUseCase;

    private LocationDTO selectedLocation;
    private List<LocationDTO> allLocations;
    private UserDTO currentUser;

    private String selectedBloodType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_request_blood, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_request_blood);
        setTitle(R.string.request_blood);

        initializeDependencies();
        initializeCurrentUser();
        initializeUI();
        setupListeners();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);

        bloodRequestUseCase = new BloodRequestUseCaseImpl(bloodRequestRepository, locationRepository);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository);
        locationTypeUseCase = new LocationTypeUseCaseImpl(locationTypeRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);

        allLocations = locationUseCase.getAllLocations();
    }

    private void initializeUI() {
        patientNameField = findViewById(R.id.patient_name);
        patientNameInputLayout = findViewById(R.id.patient_name_input_layout);
        possibleDonorsField = findViewById(R.id.possible_donors);
        deadlineField = findViewById(R.id.deadline);
        deadlineInputLayout = findViewById(R.id.deadline_input_layout);
        locationField = findViewById(R.id.location_text);
        locationInputLayout = findViewById(R.id.location_input_layout);
        bloodTypeDropdown = findViewById(R.id.blood_type_dropdown);
        bloodTypeInputLayout = findViewById(R.id.blood_type_input_layout);
        submitRequestButton = findViewById(R.id.submit_request_button);

        populateBloodTypeDropdown(bloodTypeDropdown, getResources().getStringArray(R.array.blood_types));
    }

    private void setupListeners() {
        deadlineField.setOnClickListener(v -> showDatePickerDialog());
        locationField.setOnClickListener(v -> openLocationSelectionDialog());

        bloodTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedBloodType = (String) parent.getItemAtPosition(position);
            possibleDonorsField.setText(TextUtils.join(", ", BloodRequestHelper.getCompatibleDonors(selectedBloodType)));
            clearError(bloodTypeInputLayout);
        });

        submitRequestButton.setOnClickListener(v -> validateAndSubmitRequest());

        addTextWatcher(patientNameField, patientNameInputLayout);
        addTextWatcher(deadlineField, deadlineInputLayout);
        addTextWatcher(locationField, locationInputLayout);
    }

    private void openLocationSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_location_hint);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_location, null);
        builder.setView(dialogView);

        TextInputEditText searchInput = dialogView.findViewById(R.id.search_input);
        AutoCompleteTextView locationTypeDropdown = dialogView.findViewById(R.id.location_type_dropdown);
        RecyclerView recyclerView = dialogView.findViewById(R.id.location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog dialog = builder.create();
        dialog.show();

        LocationAdapter locationAdapter = getLocationAdapter(dialog);

        recyclerView.setAdapter(locationAdapter);

        setupLocationTypeDropdown(locationTypeDropdown, locationAdapter);
        setupSearchInput(searchInput, locationAdapter);
    }

    @NonNull
    private LocationAdapter getLocationAdapter(AlertDialog dialog) {
        List<LocationDTO> allLocations = locationUseCase.getAllLocations();

        return new LocationAdapter(allLocations, new LocationAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(LocationDTO location) {
                selectedLocation = location;
                locationField.setText(location.getName());
                dialog.dismiss();
            }

            @Override
            public void onEditClick(LocationDTO location) {
            }

            @Override
            public void onDeleteClick(int locationId) {
            }
        }, true);
    }

    private void setupSearchInput(TextInputEditText searchInput, LocationAdapter locationAdapter) {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch(searchInput, locationAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterBySearch(TextInputEditText searchInput, LocationAdapter locationAdapter) {
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

    private void setupLocationTypeDropdown(AutoCompleteTextView locationTypeDropdown, LocationAdapter locationAdapter) {
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
            filterLocationsBySelectedType(locationTypes.get(position), locationAdapter);
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            deadlineField.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterLocationsBySelectedType(LocationTypeDTO selectedType, LocationAdapter locationAdapter) {
        List<LocationDTO> filteredLocations = new ArrayList<>();

        for (LocationDTO location : allLocations) {
            if (selectedType.getId() == 0 || location.getLocationTypeId() == selectedType.getId()) {
                filteredLocations.add(location);
            }
        }

        locationAdapter.setLocations(filteredLocations);
        locationAdapter.notifyDataSetChanged();
    }

    private void validateAndSubmitRequest() {
        String patientName = Objects.requireNonNull(patientNameField.getText()).toString().trim();
        String bloodType = bloodTypeDropdown.getText().toString().trim();
        String deadline = Objects.requireNonNull(deadlineField.getText()).toString().trim();
        String locationName = Objects.requireNonNull(locationField.getText()).toString().trim();

        resetErrors();

        if (!validateInputs(patientName, bloodType, deadline, locationName)) {
            return;
        }

        if (TextUtils.isEmpty(patientName) || selectedLocation == null || TextUtils.isEmpty(bloodType) || TextUtils.isEmpty(deadline)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> compatibleDonors = BloodRequestHelper.getCompatibleDonors(bloodType);
        possibleDonorsField.setText(TextUtils.join(", ", compatibleDonors));

        BloodRequestDTO bloodRequest = new BloodRequestDTO();
        bloodRequest.setPatientName(patientName);
        bloodRequest.setLocationId(selectedLocation.getId());
        bloodRequest.setLocationName(locationName);
        bloodRequest.setBloodType(bloodType);
        bloodRequest.setDeadline(deadline);
        bloodRequest.setPossibleDonors(Objects.requireNonNull(possibleDonorsField.getText()).toString());
        bloodRequest.setSubmittedBy(currentUser.getId());

        if (bloodRequestUseCase.insertBloodRequest(bloodRequest) != null) {
            Toast.makeText(this, R.string.request_submitted_successfully, Toast.LENGTH_SHORT).show();
            openHomeActivity();
        } else {
            Toast.makeText(this, R.string.error_submitting_request, Toast.LENGTH_SHORT).show();
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("userDTO", currentUser);
        intent.putExtra("bloodRequest", true);
        startActivity(intent);
        finish();
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private boolean validateInputs(String patientName, String bloodType, String deadline, String locationName) {
        boolean isValid = true;

        if (TextUtils.isEmpty(patientName)) {
            patientNameInputLayout.setError(getString(R.string.error_empty_patient_name));
            isValid = false;
        }

        if (TextUtils.isEmpty(bloodType)) {
            bloodTypeInputLayout.setError(getString(R.string.error_empty_blood_type));
            isValid = false;
        }

        if (TextUtils.isEmpty(deadline)) {
            deadlineInputLayout.setError(getString(R.string.error_empty_deadline));
            isValid = false;
        }

        if (TextUtils.isEmpty(locationName)) {
            locationInputLayout.setError(getString(R.string.error_empty_location_name));
            isValid = false;
        }

        return isValid;
    }

    private void resetErrors() {
        TextInputHelper.clearError(patientNameInputLayout);
        TextInputHelper.clearError(bloodTypeInputLayout);
        TextInputHelper.clearError(deadlineInputLayout);
        TextInputHelper.clearError(locationInputLayout);
    }

    private void populateBloodTypeDropdown(AutoCompleteTextView dropdown, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnClickListener(v -> dropdown.showDropDown());
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dropdown.showDropDown();
            }
        });
    }
}