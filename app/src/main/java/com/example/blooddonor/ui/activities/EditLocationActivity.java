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
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.domain.models.LocationTypeDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.usecases.implementation.LocationTypeUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.LocationTypeUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
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

public class EditLocationActivity extends BaseActivity {

    private AutoCompleteTextView locationTypeDropdown;
    private TextInputEditText locationSearchField, locationNameField, phoneNumbersField, cityCountryField, latitudeField, longitudeField;
    private TextInputLayout locationSearchInputLayout, locationNameInputLayout, phoneNumbersInputLayout, cityCountryInputLayout, latitudeInputLayout, longitudeInputLayout, locationTypeInputLayout;

    private LocationUseCase locationUseCase;
    private LocationTypeUseCase locationTypeUseCase;
    private String selectedLocationType;
    private LocationDTO currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_location, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_location_list);
        setTitle(R.string.edit_location);

        initializeDependencies();
        initializeUIElements();
        setupListeners();
        initializeCurrentLocation();
        populateLocationTypeDropdown();
        initializePlacesAPI();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository, bloodRequestRepository);
        locationTypeUseCase = new LocationTypeUseCaseImpl(locationTypeRepository);
    }

    private void initializeCurrentLocation() {
        currentLocation = getIntent().getParcelableExtra("location");
        if (currentLocation != null) {
            populateFieldsWithCurrentData();
        } else {
            Toast.makeText(this, R.string.error_loading_location, Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private void populateFieldsWithCurrentData() {
        locationNameField.setText(currentLocation.getName());
        phoneNumbersField.setText(currentLocation.getPhoneNumbers());
        cityCountryField.setText(currentLocation.getLocation());
        latitudeField.setText(String.valueOf(currentLocation.getLatitude()));
        longitudeField.setText(String.valueOf(currentLocation.getLongitude()));
        locationTypeDropdown.setText(currentLocation.getLocationTypeName(), false);
        selectedLocationType = currentLocation.getLocationTypeName();
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
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR && result.getData() != null) {
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Toast.makeText(this, getString(R.string.location_search_failed) + ": " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.location_search_failed, Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void populateLocationFields(Place place) {
        locationSearchField.setText(place.getFormattedAddress());
        locationNameField.setText(place.getDisplayName());

        if (place.getLocation() != null) {
            latitudeField.setText(String.valueOf(place.getLocation().latitude));
            longitudeField.setText(String.valueOf(place.getLocation().longitude));
        }

        cityCountryField.setText(place.getFormattedAddress());

        phoneNumbersField.setText("");
        locationTypeDropdown.setText("", false);
        selectedLocationType = null;
        clearError(phoneNumbersInputLayout);
        clearError(locationTypeInputLayout);
    }

    private void populateLocationTypeDropdown() {
        List<LocationTypeDTO> locationTypes = locationTypeUseCase.getAllLocationTypes();
        List<String> locationTypeNames = new ArrayList<>();
        String currentLocationTypeName = null;

        for (LocationTypeDTO locationType : locationTypes) {
            locationTypeNames.add(locationType.getName());
            if (locationType.getId() == currentLocation.getLocationTypeId()) {
                currentLocationTypeName = locationType.getName();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationTypeNames);
        locationTypeDropdown.setAdapter(adapter);

        locationTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedLocationType = (String) parent.getItemAtPosition(position);
            clearError(locationTypeInputLayout);
        });

        locationTypeDropdown.setOnClickListener(v -> locationTypeDropdown.showDropDown());
        locationTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                locationTypeDropdown.showDropDown();
            }
        });

        if (!TextUtils.isEmpty(currentLocationTypeName)) {
            locationTypeDropdown.setText(currentLocationTypeName, false);
            selectedLocationType = currentLocationTypeName;
        }
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

        if (isDataUnchanged(locationName, phoneNumbers, cityCountry, latitude, longitude, selectedLocationType)) {
            Toast.makeText(this, R.string.no_changes_detected, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentLocation.setName(locationName);
            currentLocation.setPhoneNumbers(phoneNumbers);
            currentLocation.setLocation(cityCountry);
            currentLocation.setLatitude(Double.parseDouble(latitude));
            currentLocation.setLongitude(Double.parseDouble(longitude));
            currentLocation.setLocationTypeId(getLocationTypeId(selectedLocationType));

            if (locationUseCase.updateLocation(currentLocation)) {
                Toast.makeText(this, getString(R.string.location_updated), Toast.LENGTH_SHORT).show();
                returnUpdatedLocation(currentLocation);
            } else {
                Toast.makeText(this, getString(R.string.location_update_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void returnUpdatedLocation(LocationDTO currentLocation) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("locationDTO", currentLocation);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean isDataUnchanged(String locationName, String phoneNumbers, String cityCountry, String latitude, String longitude, String locationType) {
        return locationName.equals(currentLocation.getName()) &&
                phoneNumbers.equals(currentLocation.getPhoneNumbers()) &&
                cityCountry.equals(currentLocation.getLocation()) &&
                Double.parseDouble(latitude) == currentLocation.getLatitude() &&
                Double.parseDouble(longitude) == currentLocation.getLongitude() &&
                locationType.equals(currentLocation.getLocationTypeName());
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