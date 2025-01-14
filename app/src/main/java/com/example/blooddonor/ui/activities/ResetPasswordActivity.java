package com.example.blooddonor.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
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
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.EmailSender;
import com.example.blooddonor.utils.EmailTemplates;
import com.example.blooddonor.utils.EmailValidator;
import com.example.blooddonor.utils.VerificationCodeGenerator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailField;
    private TextInputLayout emailInputLayout;
    private TextView errorMessage;
    private Button sendEmailButton;

    private UserUseCase userUseCase;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeDependencies();
        initializeUIElements();
        setupListeners();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
        emailSender = new EmailSender("support@blooddonor.com");
    }

    private void initializeUIElements() {
        emailField = findViewById(R.id.email_field);
        emailInputLayout = findViewById(R.id.email_input_layout);
        errorMessage = findViewById(R.id.error_message);
        sendEmailButton = findViewById(R.id.send_email_button);
    }

    private void setupListeners() {
        sendEmailButton.setOnClickListener(v -> handlePasswordReset());
        addTextWatcher(emailField, emailInputLayout);
    }

    private void handlePasswordReset() {
        String email = emailField.getText().toString().trim();

        emailInputLayout.setError(null);
        errorMessage.setVisibility(TextView.GONE);

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError(getString(R.string.error_empty_email));
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            emailInputLayout.setError(getString(R.string.error_invalid_email));
            return;
        }

        try {
            UserDTO user = userUseCase.getUserByEmail(email);
            if (user == null) {
                errorMessage.setText(getString(R.string.error_email_not_found));
                errorMessage.setVisibility(TextView.VISIBLE);
                return;
            }

            int verificationCode = Integer.parseInt(VerificationCodeGenerator.generateCode());

            boolean isUpdated = userUseCase.updateVerificationCode(email, verificationCode);
            if (!isUpdated) {
                Toast.makeText(this, getString(R.string.error_updating_verification_code), Toast.LENGTH_SHORT).show();
                return;
            }

            String subject = EmailTemplates.getVerificationSubject();
            String body = EmailTemplates.getVerificationEmailTemplate(String.valueOf(verificationCode), user.getFullName());

            emailSender.sendEmail(
                    email,
                    subject,
                    body,
                    new EmailSender.EmailSendCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                            navigateToVerificationCodeScreen(email);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.error_sending_email) + ": " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_sending_email), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToVerificationCodeScreen(String email) {
        Intent intent = new Intent(ResetPasswordActivity.this, EnterVerificationCodeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
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