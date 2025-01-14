package com.example.blooddonor.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.ui.adapters.LocationAdapter;

import java.util.List;

public class LocationListActivity extends BaseActivity {
    private LocationAdapter locationAdapter;
    private LocationUseCase locationUseCase;
    private RoleUseCase roleUseCase;

    private UserDTO currentUser;

    private ActivityResultLauncher<Intent> editLocationLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_location_list, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_location_list);
        setTitle(R.string.locations);

        initializeDependencies();
        initializeCurrentUser();
        initializeActivityResultLauncher();
        initializeUI();
        loadLocations();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
    }

    private void initializeUI() {
        RecyclerView recyclerView = findViewById(R.id.location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationAdapter = new LocationAdapter(locationUseCase.getAllLocations(), new LocationAdapter.OnLocationClickListener() {
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadLocations() {
        List<LocationDTO> locations = locationUseCase.getAllLocations();
        locationAdapter.setLocations(locations);
        locationAdapter.notifyDataSetChanged();
    }

    private void showLocationDetails(LocationDTO location) {
        String locationDetails = "Name: " + location.getName() + "\n" +
                "Location: " + location.getLocation() + "\n" +
                "Phone Numbers: " + location.getPhoneNumbers() + "\n" +
                "Location Type: " + location.getLocationTypeName();

        new AlertDialog.Builder(this)
                .setTitle("Location Details")
                .setMessage(locationDetails)
                .setPositiveButton("OK", null)
                .show();
    }

    private void handleDeleteLocation(int locationId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this location?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (locationUseCase.deleteLocation(locationId)) {
                        showToast("Location deleted successfully.");
                        loadLocations();
                    } else {
                        showToast("Error deleting location.");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
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