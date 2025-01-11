package com.example.blooddonor.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonor.R;

public class MainActivity extends AppCompatActivity {
    private TextView createAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIElements();
        setupListeners();
    }

    private void initializeUIElements() {
        createAccountText = findViewById(R.id.create_account);
    }

    private void setupListeners() {
        createAccountText.setOnClickListener(v -> openRegisterActivity());
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}