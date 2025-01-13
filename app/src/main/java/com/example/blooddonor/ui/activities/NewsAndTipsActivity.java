package com.example.blooddonor.ui.activities;

import android.os.Bundle;

import com.example.blooddonor.R;

public class NewsAndTipsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_news_and_tips, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_notifications);
        setTitle(R.string.notifications);
    }
}
