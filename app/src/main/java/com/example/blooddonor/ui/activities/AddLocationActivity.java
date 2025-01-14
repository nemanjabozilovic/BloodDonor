package com.example.blooddonor.ui.activities;

import static com.example.blooddonor.utils.TextInputHelper.clearError;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.domain.models.LocationDTO;
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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddLocationActivity extends BaseActivity {

    private AutoCompleteTextView locationTypeDropdown;
    private TextInputEditText locationSearchField, locationNameField, phoneNumbersField, cityCountryField, latitudeField, longitudeField;
    private TextInputLayout locationSearchInputLayout, locationNameInputLayout, phoneNumbersInputLayout, cityCountryInputLayout, latitudeInputLayout, longitudeInputLayout, locationTypeInputLayout;

    private LocationUseCase locationUseCase;
    private LocationTypeUseCase locationTypeUseCase;
    private RoleUseCase roleUseCase;

    private String selectedLocationType;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_location, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_add_location);
        setTitle(R.string.add_location);

        initializeDependencies();
        initializeCurrentUser();
        initializeUIElements();
        setupListeners();
        populateLocationTypeDropdown();
        initializePlacesAPI();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository);
        locationTypeUseCase = new LocationTypeUseCaseImpl(locationTypeRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeUIElements() {
        locationTypeDropdown = findViewById(R.id.location_type_dropdown);
        locationSearchField = findViewById(R.id.location_search);
        locationNameField = findViewById(R.id.location_name);
        phoneNumbersField = findViewById(R.id.phone_numbers);
        cityCountryField = findViewById(R.id.city_country);
        latitudeField = findViewById(R.id.latitude);
        longitudeField = findViewById(R.id.longitude);

        locationSearchInputLayout = findViewById(R.id.location_search_input_layout);
        locationNameInputLayout = findViewById(R.id.location_name_input_layout);
        phoneNumbersInputLayout = findViewById(R.id.phone_numbers_input_layout);
        cityCountryInputLayout = findViewById(R.id.city_country_input_layout);
        latitudeInputLayout = findViewById(R.id.latitude_input_layout);
        longitudeInputLayout = findViewById(R.id.longitude_input_layout);
        locationTypeInputLayout = findViewById(R.id.location_type_input_layout);
    }

    private void populateLocationTypeDropdown() {
        List<LocationTypeDTO> locationTypes = locationTypeUseCase.getAllLocationTypes();
        List<String> locationTypeNames = new ArrayList<>();

        for (LocationTypeDTO locationType : locationTypes) {
            locationTypeNames.add(locationType.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationTypeNames);
        locationTypeDropdown.setAdapter(adapter);
        locationTypeDropdown.setOnClickListener(v -> locationTypeDropdown.showDropDown());
        locationTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                locationTypeDropdown.showDropDown();
            }
        });
    }

    private void setupListeners() {
        locationSearchField.setOnClickListener(v -> startLocationSearch());

        locationTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedLocationType = (String) parent.getItemAtPosition(position);
            clearError(locationTypeInputLayout);
        });

        locationNameField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !TextUtils.isEmpty(locationNameField.getText())) {
                clearError(locationNameInputLayout);
            }
        });

        phoneNumbersField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    clearError(phoneNumbersInputLayout);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.save_location_button).setOnClickListener(v -> handleSaveLocation());
    }

    private void startLocationSearch() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                List.of(
                        Place.Field.ID,
                        Place.Field.DISPLAY_NAME,
                        Place.Field.FORMATTED_ADDRESS,
                        Place.Field.LOCATION
                )
        ).build(this);
        startActivityIntent.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    populateLocationFields(place);
                    clearError(locationSearchInputLayout);
                    clearError(locationNameInputLayout);
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR && result.getData() != null) {
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Toast.makeText(this, getString(R.string.location_search_failed) + ": " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.location_search_failed, Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateLocationFields(Place place) {
        locationSearchField.setText(place.getFormattedAddress());
        locationNameField.setText(place.getDisplayName());

        if (place.getLocation() != null) {
            latitudeField.setText(String.valueOf(place.getLocation().latitude));
            longitudeField.setText(String.valueOf(place.getLocation().longitude));
        }

        cityCountryField.setText(place.getFormattedAddress());
    }

    private void handleSaveLocation() {
        String locationName = getFieldValue(locationNameField);
        String phoneNumbers = getFieldValue(phoneNumbersField);
        String cityCountry = getFieldValue(cityCountryField);
        String latitude = getFieldValue(latitudeField);
        String longitude = getFieldValue(longitudeField);

        resetErrors();

        if (!validateInputs(locationName, phoneNumbers, cityCountry, latitude, longitude, selectedLocationType)) {
            return;
        }

        try {
            LocationDTO location = new LocationDTO();
            location.setName(locationName);
            location.setPhoneNumbers(phoneNumbers);
            location.setLocation(cityCountry);
            location.setLongitude(Double.parseDouble(latitude));
            location.setLatitude(Double.parseDouble(longitude));
            location.setLocationTypeId(getLocationTypeId(selectedLocationType));

            if (locationUseCase.insertLocation(location) != null) {
                Toast.makeText(this, getString(R.string.location_saved), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LocationListActivity.class);
                intent.putExtra("userDTO", currentUser);
                intent.putExtra("location", location);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, getString(R.string.location_save_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFieldValue(TextInputEditText field) {
        return Objects.requireNonNull(field.getText()).toString().trim();
    }

    private int getLocationTypeId(String locationType) {
        return locationTypeUseCase.getLocationTypeByName(locationType).getId();
    }

    private void resetErrors() {
        clearError(locationSearchInputLayout);
        clearError(locationNameInputLayout);
        clearError(phoneNumbersInputLayout);
        clearError(cityCountryInputLayout);
        clearError(latitudeInputLayout);
        clearError(longitudeInputLayout);
        clearError(locationTypeInputLayout);
    }

    private boolean validateInputs(String locationName, String phoneNumbers, String cityCountry, String latitude, String longitude, String locationType) {
        if (TextUtils.isEmpty(locationName)) {
            locationNameInputLayout.setError(getString(R.string.error_empty_location_name));
            return false;
        }

        if (TextUtils.isEmpty(phoneNumbers)) {
            phoneNumbersInputLayout.setError(getString(R.string.error_empty_phone_numbers));
            return false;
        }

        if (TextUtils.isEmpty(cityCountry)) {
            cityCountryInputLayout.setError(getString(R.string.error_empty_city_country));
            return false;
        }

        if (TextUtils.isEmpty(latitude)) {
            latitudeInputLayout.setError(getString(R.string.error_empty_latitude));
            return false;
        }

        if (TextUtils.isEmpty(longitude)) {
            longitudeInputLayout.setError(getString(R.string.error_empty_longitude));
            return false;
        }

        if (TextUtils.isEmpty(locationType)) {
            locationTypeInputLayout.setError(getString(R.string.error_empty_location_type));
            return false;
        }

        return true;
    }

    private void initializePlacesAPI() {
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
    }
}