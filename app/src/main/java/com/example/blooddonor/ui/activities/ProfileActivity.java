package com.example.blooddonor.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.implementation.BloodRequestUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.ui.adapters.BloodRequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    private BloodRequestAdapter bloodRequestAdapter;
    private BloodRequestUseCase bloodRequestUseCase;
    private RoleUseCase roleUseCase;

    private ImageView profileImage;
    private TextView userName;
    private TextView userEmail;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_profile);
        setTitle(R.string.profile);

        initializeDependencies();
        initializeCurrentUser();
        initializeUIElements();
        loadUserData();
        loadBloodRequests();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        bloodRequestUseCase = new BloodRequestUseCaseImpl(bloodRequestRepository, locationRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void initializeUIElements() {
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        RecyclerView bloodRequestRecyclerView = findViewById(R.id.blood_requests_recycler_view);
        bloodRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bloodRequestAdapter = new BloodRequestAdapter(new ArrayList<>());
        bloodRequestRecyclerView.setAdapter(bloodRequestAdapter);
    }

    private void loadUserData() {
        if (currentUser != null) {
            userName.setText(currentUser.getFullName());
            userEmail.setText(currentUser.getEmail());

            Glide.with(this)
                    .load(currentUser.getProfilePicture())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profileImage);
        }
    }

    private void loadBloodRequests() {
        List<BloodRequestDTO> bloodRequests = bloodRequestUseCase.getAllBloodRequests();
        bloodRequestAdapter.updateData(bloodRequests);
    }
}