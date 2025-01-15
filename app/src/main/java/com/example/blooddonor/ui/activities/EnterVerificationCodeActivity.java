package com.example.blooddonor.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EnterVerificationCodeActivity extends AppCompatActivity {

    private TextInputEditText verificationCodeField;
    private TextInputLayout verificationCodeInputLayout;
    private TextView errorMessage;
    private Button verifyButton;

    private UserUseCase userUseCase;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_verification_code);

        enterFullScreenMode();
        initializeDependencies();
        initializeUIElements();
        setupListeners();
        getUserEmailFromIntent();
    }

    private void enterFullScreenMode() {
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
    }

    private void initializeUIElements() {
        verificationCodeField = findViewById(R.id.verification_code_field);
        verificationCodeInputLayout = findViewById(R.id.verification_code_input_layout);
        errorMessage = findViewById(R.id.error_message);
        verifyButton = findViewById(R.id.verify_button);
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> handleVerification());
    }

    private void getUserEmailFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userEmail = intent.getStringExtra("email");
        }
    }

    private void handleVerification() {
        String verificationCode = verificationCodeField.getText().toString().trim();

        verificationCodeInputLayout.setError(null);
        errorMessage.setVisibility(TextView.GONE);

        if (TextUtils.isEmpty(verificationCode)) {
            verificationCodeInputLayout.setError(getString(R.string.error_empty_verification_code));
            return;
        }

        try {
            UserDTO user = userUseCase.getUserByEmail(userEmail);

            if (user == null) {
                errorMessage.setText(getString(R.string.error_user_not_found));
                errorMessage.setVisibility(TextView.VISIBLE);
                return;
            }

            if (user.getVerificationCode() == Integer.parseInt(verificationCode)) {
                Toast.makeText(this, getString(R.string.verification_successful), Toast.LENGTH_SHORT).show();
                navigateToChangePasswordScreen(userEmail);
            } else {
                errorMessage.setText(getString(R.string.error_invalid_verification_code));
                errorMessage.setVisibility(TextView.VISIBLE);
            }
        } catch (NumberFormatException e) {
            verificationCodeInputLayout.setError(getString(R.string.error_invalid_verification_code));
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_verification_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToChangePasswordScreen(String email) {
        Intent intent = new Intent(EnterVerificationCodeActivity.this, ChangePasswordActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}