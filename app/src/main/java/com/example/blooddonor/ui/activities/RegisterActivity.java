package com.example.blooddonor.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.RoleDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.EmailValidator;
import com.example.blooddonor.utils.PasswordUtils;
import com.example.blooddonor.utils.UserAlreadyExistsException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView bloodTypeDropdown, roleDropdown;
    private TextInputEditText fullNameField, emailField, passwordField;
    private TextInputLayout fullNameInputLayout, emailInputLayout, passwordInputLayout, bloodTypeInputLayout, roleInputLayout;
    private Button registerButton;

    private UserUseCase userUseCase;
    private RoleUseCase roleUseCase;

    private String selectedBloodType;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeDependencies();
        initializeUIElements();
        setupListeners();
        populateRoleDropdown();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);

        roleUseCase = new RoleUseCaseImpl(roleRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleUseCase);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeUIElements() {
        bloodTypeDropdown = findViewById(R.id.blood_type_dropdown);
        roleDropdown = findViewById(R.id.user_role_dropdown);
        fullNameField = findViewById(R.id.full_name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        fullNameInputLayout = findViewById(R.id.full_name_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        bloodTypeInputLayout = findViewById(R.id.blood_type_input_layout);
        roleInputLayout = findViewById(R.id.user_role_input_layout);

        registerButton = findViewById(R.id.register_button);

        setupDropdown(bloodTypeDropdown, getResources().getStringArray(R.array.blood_types));
    }

    private void setupDropdown(AutoCompleteTextView dropdown, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnClickListener(v -> dropdown.showDropDown());
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dropdown.showDropDown();
            }
        });
    }

    private void populateRoleDropdown() {
        List<RoleDTO> roles = roleUseCase.getAllRoles();
        List<String> roleNames = extractRoleNames(roles);

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roleNames);
        roleDropdown.setAdapter(roleAdapter);
        roleDropdown.setOnClickListener(v -> roleDropdown.showDropDown());
        roleDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                roleDropdown.showDropDown();
            }
        });
    }

    private List<String> extractRoleNames(List<RoleDTO> roles) {
        List<String> roleNames = new ArrayList<>();
        for (RoleDTO role : roles) {
            if (!role.getRoleName().equalsIgnoreCase("Admin")) {
                roleNames.add(role.getRoleName());
            }
        }
        return roleNames;
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegistration());

        bloodTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedBloodType = (String) parent.getItemAtPosition(position);
            clearError(bloodTypeInputLayout);
        });

        roleDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = (String) parent.getItemAtPosition(position);
            clearError(roleInputLayout);
        });

        addTextWatcher(fullNameField, fullNameInputLayout);
        addTextWatcher(emailField, emailInputLayout);
        addTextWatcher(passwordField, passwordInputLayout);
    }

    private void handleRegistration() {
        String fullName = getFieldValue(fullNameField);
        String email = getFieldValue(emailField);
        String password = getFieldValue(passwordField);

        resetErrors();

        if (!validateInputs(fullName, email, password, selectedBloodType, selectedRole)) {
            return;
        }

        try {
            UserDTO newUser = createUserDTO(fullName, email, password);
            UserDTO createdUser = userUseCase.insertUser(newUser);

            if (createdUser != null) {
                showToast(getString(R.string.register_success));
                navigateToHome(createdUser);
            } else {
                showToast(getString(R.string.register_error));
            }
        } catch (UserAlreadyExistsException e) {
            emailInputLayout.setError(e.getMessage());
        } catch (Exception e) {
            showToast(getString(R.string.register_error));
        }
    }

    private String getFieldValue(TextInputEditText field) {
        return Objects.requireNonNull(field.getText()).toString().trim();
    }

    private void resetErrors() {
        clearError(fullNameInputLayout);
        clearError(emailInputLayout);
        clearError(passwordInputLayout);
        clearError(bloodTypeInputLayout);
        clearError(roleInputLayout);
    }

    private boolean validateInputs(String fullName, String email, String password, String bloodType, String role) {
        if (TextUtils.isEmpty(fullName)) {
            fullNameInputLayout.setError(getString(R.string.error_empty_name));
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError(getString(R.string.error_empty_email));
            return false;
        }

        if (!EmailValidator.isValidEmail(email)) {
            emailInputLayout.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError(getString(R.string.error_empty_password));
            return false;
        }

        if (TextUtils.isEmpty(bloodType)) {
            bloodTypeInputLayout.setError(getString(R.string.error_empty_blood_type));
            return false;
        }

        if (TextUtils.isEmpty(role)) {
            roleInputLayout.setError(getString(R.string.error_empty_role));
            return false;
        }

        return true;
    }

    private UserDTO createUserDTO(String fullName, String email, String password) {
        String salt = PasswordUtils.generateSalt();
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(fullName);
        userDTO.setEmail(email);
        userDTO.setSalt(salt);
        userDTO.setPassword(PasswordUtils.hashPassword(password, salt));
        userDTO.setBloodType(selectedBloodType);

        int roleId = roleUseCase.getRoleIdByName(selectedRole);
        if (roleId == -1) {
            showToast(getString(R.string.register_error));
            return null;
        }
        userDTO.setRoleId(roleId);

        return userDTO;
    }

    private void navigateToHome(UserDTO createdUser) {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.putExtra("userDTO", createdUser);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addTextWatcher(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    clearError(inputLayout);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void clearError(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }
}