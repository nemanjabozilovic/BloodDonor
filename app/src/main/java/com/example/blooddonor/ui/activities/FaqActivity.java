package com.example.blooddonor.ui.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.FaqRepositoryImpl;
import com.example.blooddonor.domain.usecases.implementation.FaqUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.FaqUseCase;
import com.example.blooddonor.ui.adapters.FaqAdapter;

public class FaqActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_faq, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_faq);
        setTitle(R.string.faq);

        RecyclerView faqRecyclerView = findViewById(R.id.faq_recycler_view);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        FaqUseCase faqUseCase = new FaqUseCaseImpl(new FaqRepositoryImpl(dbHelper));

        FaqAdapter faqAdapter = new FaqAdapter(faqUseCase.getAllFaqs());
        faqRecyclerView.setAdapter(faqAdapter);
    }
}