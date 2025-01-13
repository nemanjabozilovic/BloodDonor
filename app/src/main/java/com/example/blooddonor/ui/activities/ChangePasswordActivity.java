package com.example.blooddonor.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.PasswordUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText newPasswordField, confirmPasswordField;
    private TextInputLayout newPasswordInputLayout, confirmPasswordInputLayout;
    private String userEmail;
    private UserUseCase userUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initializeDependencies();
        initializeUIElements();
        setupListeners();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);

        userEmail = getIntent().getStringExtra("email");
    }

    private void initializeUIElements() {
        newPasswordField = findViewById(R.id.new_password_field);
        confirmPasswordField = findViewById(R.id.confirm_password_field);
        newPasswordInputLayout = findViewById(R.id.new_password_input_layout);
        confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout);
    }

    private void setupListeners() {
        findViewById(R.id.change_password_button).setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        clearErrors();

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInputLayout.setError(getString(R.string.error_empty_password));
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.error_empty_password));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.error_passwords_not_matching));
            return;
        }

        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(newPassword, salt);

        boolean isUpdated = userUseCase.updatePassword(userEmail, hashedPassword, salt);

        if (isUpdated) {
            Toast.makeText(this, getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_updating_password), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        newPasswordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
    }
}