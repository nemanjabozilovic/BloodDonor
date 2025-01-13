package com.example.blooddonor.ui.activities;

import android.os.Bundle;

import com.example.blooddonor.R;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_home);
        setTitle(R.string.home);
    }
}