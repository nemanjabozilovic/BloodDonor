package com.example.blooddonor.ui.activities;

import android.os.Bundle;

import com.example.blooddonor.R;

public class RequestBloodActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_request_blood, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_request_blood);
        setTitle(R.string.request_blood);
    }
}