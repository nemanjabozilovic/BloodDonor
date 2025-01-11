package com.example.blooddonor.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.EmailValidator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView createAccountText, errorText;
    private TextInputLayout emailInputLayout, passwordInputLayout;

    private UserUseCase userUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDependencies();
        initializeUIElements();
        setupListeners();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        RoleUseCase roleUseCase = new RoleUseCaseImpl(roleRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleUseCase);
    }

    private void initializeUIElements() {
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        createAccountText = findViewById(R.id.create_account);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        errorText = findViewById(R.id.invalid_credentials_message);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        createAccountText.setOnClickListener(v -> openRegisterActivity());

        addTextWatcher(emailField, emailInputLayout);
        addTextWatcher(passwordField, passwordInputLayout);
    }

    private void handleLogin() {
        String email = getFieldValue(emailField);
        String password = getFieldValue(passwordField);

        resetErrors();

        if (!validateInputs(email, password)) {
            return;
        }

        try {
            UserDTO userDTO = userUseCase.login(email, password);

            if (userDTO != null) {
                navigateToHome(userDTO);
            } else {
                showError(getString(R.string.invalid_credentials));
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private String getFieldValue(EditText field) {
        return Objects.requireNonNull(field.getText()).toString().trim();
    }

    private void resetErrors() {
        clearError(emailInputLayout);
        clearError(passwordInputLayout);
        errorText.setVisibility(View.GONE);
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError(getString(R.string.error_empty_email));
            return false;
        }

        if(!EmailValidator.isValidEmail(email)) {
            emailInputLayout.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError(getString(R.string.error_empty_password));
            return false;
        }

        return true;
    }

    private void navigateToHome(UserDTO userDTO) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("userDTO", userDTO);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void addTextWatcher(EditText editText, TextInputLayout inputLayout) {
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