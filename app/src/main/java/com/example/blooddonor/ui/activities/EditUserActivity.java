package com.example.blooddonor.ui.activities;

import static com.example.blooddonor.utils.TextInputHelper.addTextWatcher;
import static com.example.blooddonor.utils.TextInputHelper.clearError;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditUserActivity extends BaseActivity {

    private RoleUseCase roleUseCase;
    private UserUseCase userUseCase;

    private TextInputEditText fullNameField, emailField, dateOfBirthField;
    private AutoCompleteTextView roleDropdown, bloodTypeDropdown;
    private TextInputLayout fullNameInputLayout, emailInputLayout, dateOfBirthInputLayout, roleInputLayout, bloodTypeInputLayout;
    private MaterialButton saveUserButton, change_password_button;

    private UserDTO currentUser, selectedUser;
    private String selectedRole, selectedBloodType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_user, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_users);
        setTitle(R.string.edit_user);

        initializeDependencies();
        initializeCurrentUser();
        initializeSelectedUser();
        initializeUIElements();
        setupListeners();
        populateUserData();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);

        roleUseCase = new RoleUseCaseImpl(roleRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void initializeSelectedUser() {
        selectedUser = getIntent().getParcelableExtra("user");
        if (selectedUser == null) {
            Toast.makeText(this, R.string.no_selected_user_data_found, Toast.LENGTH_SHORT).show();
        }
        selectedUser.setRoleName(roleUseCase.getRoleById(selectedUser.getRoleId()).getRoleName());
    }

    private void initializeUIElements() {
        fullNameField = findViewById(R.id.full_name);
        emailField = findViewById(R.id.email);
        dateOfBirthField = findViewById(R.id.date_of_birth);
        roleDropdown = findViewById(R.id.user_role_dropdown);
        bloodTypeDropdown = findViewById(R.id.blood_type_dropdown);

        fullNameInputLayout = findViewById(R.id.full_name_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        dateOfBirthInputLayout = findViewById(R.id.date_of_birth_input_layout);
        roleInputLayout = findViewById(R.id.user_role_input_layout);
        bloodTypeInputLayout = findViewById(R.id.blood_type_input_layout);
        saveUserButton = findViewById(R.id.save_user_button);
        change_password_button = findViewById(R.id.change_password_button);

        populateBloodTypeDropdown();
        populateRoleDropdown();
    }

    private void populateUserData() {
        if (selectedUser != null) {
            fullNameField.setText(selectedUser.getFullName());
            emailField.setText(selectedUser.getEmail());
            dateOfBirthField.setText(selectedUser.getDateOfBirth());
            roleDropdown.setText(selectedUser.getRoleName(), false);
            bloodTypeDropdown.setText(selectedUser.getBloodType(), false);
            selectedRole = selectedUser.getRoleName();
            selectedBloodType = selectedUser.getBloodType();
        }
    }

    private void setupListeners() {
        change_password_button.setOnClickListener(v -> showChangePasswordDialog());
        saveUserButton.setOnClickListener(v -> handleSaveUser());

        bloodTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedBloodType = (String) parent.getItemAtPosition(position);
            clearError(bloodTypeInputLayout);
        });

        roleDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = (String) parent.getItemAtPosition(position);
            clearError(roleInputLayout);
        });

        dateOfBirthField.setOnClickListener(v -> showDatePickerDialog());

        addTextWatcher(fullNameField, fullNameInputLayout);
        addTextWatcher(emailField, emailInputLayout);
        addTextWatcher(dateOfBirthField, dateOfBirthInputLayout);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            dateOfBirthField.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void populateBloodTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.blood_types));
        bloodTypeDropdown.setAdapter(adapter);
        bloodTypeDropdown.setOnClickListener(v -> bloodTypeDropdown.showDropDown());
        bloodTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bloodTypeDropdown.showDropDown();
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

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_password);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextInputEditText newPasswordField = dialogView.findViewById(R.id.new_password_field);
        TextInputEditText confirmPasswordField = dialogView.findViewById(R.id.confirm_password_field);
        TextInputLayout newPasswordInputLayout = dialogView.findViewById(R.id.new_password_input_layout);
        TextInputLayout confirmPasswordInputLayout = dialogView.findViewById(R.id.confirm_password_input_layout);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String newPassword = Objects.requireNonNull(newPasswordField.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmPasswordField.getText()).toString().trim();

            clearError(newPasswordInputLayout);
            clearError(confirmPasswordInputLayout);

            if (TextUtils.isEmpty(newPassword)) {
                newPasswordInputLayout.setError(getString(R.string.error_empty_new_password));
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                confirmPasswordInputLayout.setError(getString(R.string.error_empty_confirm_password));
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInputLayout.setError(getString(R.string.error_passwords_not_matching));
                return;
            }

            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(newPassword, salt);

            selectedUser.setPassword(hashedPassword);
            selectedUser.setSalt(salt);

            Toast.makeText(this, R.string.new_password_saved, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void handleSaveUser() {
        String fullName = Objects.requireNonNull(fullNameField.getText()).toString().trim();
        String email = Objects.requireNonNull(emailField.getText()).toString().trim();
        String dateOfBirth = Objects.requireNonNull(dateOfBirthField.getText()).toString().trim();

        resetErrors();

        if (!validateInputs(fullName, email, dateOfBirth)) {
            return;
        }

        selectedUser.setFullName(fullName);
        selectedUser.setEmail(email);
        selectedUser.setDateOfBirth(dateOfBirth);
        selectedUser.setRoleName(selectedRole);
        selectedUser.setBloodType(selectedBloodType);

        if (userUseCase.updateUser(selectedUser) != null) {
            Toast.makeText(this, R.string.user_updated_successfully, Toast.LENGTH_SHORT).show();
            returnUpdatedUser(selectedUser);
        } else {
            Toast.makeText(this, R.string.error_updating_user, Toast.LENGTH_SHORT).show();
        }
    }

    private void returnUpdatedUser(UserDTO selectedUser) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", selectedUser);
        resultIntent.putExtra("userDTO", currentUser);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean validateInputs(String fullName, String email, String dateOfBirth) {
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

        if (TextUtils.isEmpty(dateOfBirth)) {
            dateOfBirthInputLayout.setError(getString(R.string.error_empty_date_of_birth));
            return false;
        }

        return true;
    }

    private void resetErrors() {
        clearError(fullNameInputLayout);
        clearError(emailInputLayout);
        clearError(dateOfBirthInputLayout);
        clearError(roleInputLayout);
        clearError(bloodTypeInputLayout);
    }
}