package com.example.blooddonor.ui.activities;

import android.os.Bundle;

import com.example.blooddonor.R;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_profile);
        setTitle(R.string.profile);
    }
}