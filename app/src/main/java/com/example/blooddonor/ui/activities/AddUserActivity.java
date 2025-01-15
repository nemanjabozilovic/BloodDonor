package com.example.blooddonor.ui.activities;

import static com.example.blooddonor.utils.TextInputHelper.addTextWatcher;
import static com.example.blooddonor.utils.TextInputHelper.clearError;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.blooddonor.utils.EmailSender;
import com.example.blooddonor.utils.EmailTemplates;
import com.example.blooddonor.utils.EmailValidator;
import com.example.blooddonor.utils.PasswordUtils;
import com.example.blooddonor.utils.TextInputHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddUserActivity extends BaseActivity {

    private TextInputEditText fullNameField, emailField, passwordField;
    private AutoCompleteTextView roleDropdown, bloodTypeDropdown;
    private TextInputLayout fullNameInputLayout, emailInputLayout, passwordInputLayout, roleInputLayout, bloodTypeInputLayout;
    private MaterialButton saveUserButton;

    private RoleUseCase roleUseCase;
    private UserUseCase userUseCase;
    private EmailSender emailSender;

    private String selectedRole, selectedBloodType;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_user, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_add_user);
        setTitle(R.string.add_user);

        initializeDependencies();
        initializeCurrentUser();
        initializeUIElements();
        setupListeners();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);

        roleUseCase = new RoleUseCaseImpl(roleRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);

        emailSender = new EmailSender("office@blooddonor.com");
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
        fullNameField = findViewById(R.id.full_name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        roleDropdown = findViewById(R.id.user_role_dropdown);
        bloodTypeDropdown = findViewById(R.id.blood_type_dropdown);

        fullNameInputLayout = findViewById(R.id.full_name_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        roleInputLayout = findViewById(R.id.user_role_input_layout);
        bloodTypeInputLayout = findViewById(R.id.blood_type_input_layout);

        saveUserButton = findViewById(R.id.save_user_button);

        populateBloodTypeDropdown(bloodTypeDropdown, getResources().getStringArray(R.array.blood_types));
        populateRoleDropdown();
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
        saveUserButton.setOnClickListener(v -> handleSaveUser());

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

    private void handleSaveUser() {
        String fullName = getFieldValue(fullNameField);
        String email = getFieldValue(emailField);
        String password = getFieldValue(passwordField);

        resetErrors();

        if (!validateInputs(fullName, email, password, selectedRole, selectedBloodType)) {
            return;
        }

        try {
            UserDTO newUser = createUserDTO(fullName, email, password);
            UserDTO createdUser = userUseCase.insertUser(newUser);

            if (createdUser != null) {
                Toast.makeText(this, getString(R.string.user_saved_successfully), Toast.LENGTH_SHORT).show();
                sendWelcomeEmail(createdUser);
                openUserListActivity(createdUser);
            } else {
                Toast.makeText(this, getString(R.string.user_save_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendWelcomeEmail(UserDTO user) {
        String subject = EmailTemplates.getWelcomeSubject();
        String body = EmailTemplates.getNewUserWelcomeTemplate(user.getFullName());
        String email = user.getEmail();

        emailSender.sendEmail(
                email,
                subject,
                body,
                new EmailSender.EmailSendCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                    }
                }
        );
    }

    private void openUserListActivity(UserDTO createdUser) {
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("userDTO", currentUser);
        intent.putExtra("createdUser", createdUser);
        startActivity(intent);
        finish();
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
        userDTO.setRoleId(roleId);

        return userDTO;
    }

    private void resetErrors() {
        TextInputHelper.clearError(fullNameInputLayout);
        TextInputHelper.clearError(emailInputLayout);
        TextInputHelper.clearError(passwordInputLayout);
        TextInputHelper.clearError(roleInputLayout);
        TextInputHelper.clearError(bloodTypeInputLayout);
    }

    private boolean validateInputs(String fullName, String email, String password, String role, String bloodType) {
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

        if (TextUtils.isEmpty(role)) {
            roleInputLayout.setError(getString(R.string.error_empty_role));
            return false;
        }

        if (TextUtils.isEmpty(bloodType)) {
            bloodTypeInputLayout.setError(getString(R.string.error_empty_blood_type));
            return false;
        }

        return true;
    }

    private String getFieldValue(TextInputEditText field) {
        return Objects.requireNonNull(field.getText()).toString().trim();
    }
}