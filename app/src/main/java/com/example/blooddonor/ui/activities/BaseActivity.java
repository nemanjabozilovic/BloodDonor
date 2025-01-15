package com.example.blooddonor.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private RoleUseCase roleUseCase;

    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initializeDependencies();
        initializeCurrentUser();

        setupToolbar();
        setupDrawer();
        setupNavigationView();
        onBackPressedDispatcher();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation_view);

        if ("admin".equalsIgnoreCase(currentUser.getRoleName())) {
            navigationView.getMenu().findItem(R.id.nav_location_list).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_add_location).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_users).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_add_user).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });

        initializeNavHeader(navigationView);
    }

    @SuppressLint("NonConstantResourceId")
    private void handleNavigationItemSelected(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                navigateToActivity(HomeActivity.class);
                break;
            case R.id.nav_profile:
                navigateToActivity(ProfileActivity.class);
                break;
            case R.id.nav_location_list:
                navigateToActivity(LocationListActivity.class);
                break;
            case R.id.nav_add_location:
                navigateToActivity(AddLocationActivity.class);
                break;
            case R.id.nav_users:
                navigateToActivity(UserListActivity.class);
                break;
            case R.id.nav_add_user:
                navigateToActivity(AddUserActivity.class);
                break;
            case R.id.nav_request_blood:
                navigateToActivity(RequestBloodActivity.class);
                break;
            case R.id.nav_notifications:
                navigateToActivity(NotificationActivity.class);
                break;
            case R.id.nav_faq:
                navigateToActivity(FaqActivity.class);
                break;
            case R.id.nav_logout:
                handleLogout();
                break;
            default:
                break;
        }
    }

    private void initializeNavHeader(NavigationView navigationView) {
        android.view.View headerView = navigationView.getHeaderView(0);

        ImageView profileImage = headerView.findViewById(R.id.profile_image);
        TextView userName = headerView.findViewById(R.id.user_name);
        TextView userEmail = headerView.findViewById(R.id.user_email);

        userName.setText(currentUser.getFullName());
        userEmail.setText(currentUser.getEmail());
        profileImage.setImageResource(R.drawable.ic_profile_placeholder);
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("userDTO", currentUser);
        startActivity(intent);
    }

    protected void setActiveMenuItem(int menuItemId) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(menuItemId).setChecked(true);
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);

        roleUseCase = new RoleUseCaseImpl(roleRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void handleLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
        System.exit(0);
    }

    private void onBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    protected void refreshNavHeader(UserDTO updatedCurrentUser) {
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null && updatedCurrentUser != null) {
            ImageView profileImage = headerView.findViewById(R.id.profile_image);
            TextView userName = headerView.findViewById(R.id.user_name);
            TextView userEmail = headerView.findViewById(R.id.user_email);

            userName.setText(updatedCurrentUser.getFullName());
            userEmail.setText(updatedCurrentUser.getEmail());
            profileImage.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }
}