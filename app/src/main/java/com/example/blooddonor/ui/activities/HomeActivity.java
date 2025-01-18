package com.example.blooddonor.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.BloodRequestUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.ui.adapters.BloodRequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private BloodRequestAdapter bloodRequestAdapter;
    private BloodRequestUseCase bloodRequestUseCase;
    private RoleUseCase roleUseCase;
    private LocationUseCase locationUseCase;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_home);
        setTitle(R.string.home);

        initializeDependencies();
        initializeCurrentUser();
        initializeUIElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBloodRequests();

        boolean shouldRefresh = getIntent().getBooleanExtra("bloodRequest", false);
        if (shouldRefresh) {
            loadBloodRequests();
            getIntent().removeExtra("bloodRequest");
        }
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        bloodRequestUseCase = new BloodRequestUseCaseImpl(bloodRequestRepository, locationRepository, userRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository, bloodRequestRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void initializeUIElements() {
        RecyclerView bloodRequestRecyclerView = findViewById(R.id.blood_requests_recycler_view);
        bloodRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bloodRequestAdapter = new BloodRequestAdapter(new ArrayList<>(), locationUseCase);
        bloodRequestRecyclerView.setAdapter(bloodRequestAdapter);
    }

    private void loadBloodRequests() {
        List<BloodRequestDTO> bloodRequests = bloodRequestUseCase.getAllBloodRequests();
        bloodRequestAdapter.updateData(bloodRequests);
    }
}