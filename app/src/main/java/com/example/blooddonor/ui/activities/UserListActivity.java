package com.example.blooddonor.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.RoleDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.ui.adapters.UserAdapter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends BaseActivity {

    private UserAdapter userAdapter;
    private MaterialAutoCompleteTextView userTypeDropdown;
    private TextInputEditText searchInput;
    private UserUseCase userUseCase;
    private RoleUseCase roleUseCase;
    private List<UserDTO> allUsers;

    private UserDTO currentUser;

    private ActivityResultLauncher<Intent> editUserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_user_list, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_users);
        setTitle(R.string.users);

        initializeDependencies();
        initializeCurrentUser();
        initializeActivityResultLauncher();
        initializeUIElements();
        loadUsers();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);

        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
    }

    private void initializeUIElements() {
        RecyclerView recyclerView = findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new ArrayList<>(), new UserAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(UserDTO user) {
                showUserDetails(user);
            }

            @Override
            public void onEditClick(UserDTO user) {
                openEditUserActivity(user);
            }

            @Override
            public void onDeleteClick(int userId) {
                handleDeleteUser(userId);
            }
        });

        recyclerView.setAdapter(userAdapter);

        userTypeDropdown = findViewById(R.id.user_type_dropdown);
        searchInput = findViewById(R.id.search_input);

        setupUserTypeDropdown();
        setupSearchInput();
    }

    private void setupUserTypeDropdown() {
        List<RoleDTO> roles = roleUseCase.getAllRoles();

        RoleDTO allRole = new RoleDTO();
        allRole.setId(0);
        allRole.setRoleName(getString(R.string.all));
        roles.add(0, allRole);

        List<String> roleNames = new ArrayList<>();
        for (RoleDTO role : roles) {
            roleNames.add(role.getRoleName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roleNames);
        userTypeDropdown.setAdapter(adapter);

        userTypeDropdown.setOnClickListener(v -> userTypeDropdown.showDropDown());
        userTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                userTypeDropdown.showDropDown();
            }
        });

        userTypeDropdown.setOnItemClickListener((parent, view, position, id) -> filterUsersByRole(roles.get(position)));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterUsersByRole(RoleDTO selectedRole) {
        List<UserDTO> filteredUsers = new ArrayList<>();
        for (UserDTO user : allUsers) {
            if (selectedRole.getId() == 0 || user.getRoleId() == selectedRole.getId()) {
                filteredUsers.add(user);
            }
        }

        userAdapter.setUsers(filteredUsers);
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsersBySearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterUsersBySearch() {
        String query = Objects.requireNonNull(searchInput.getText()).toString().toLowerCase();

        List<UserDTO> filteredUsers = new ArrayList<>();
        for (UserDTO user : allUsers) {
            if (user.getFullName().toLowerCase().contains(query)) {
                filteredUsers.add(user);
            }
        }

        userAdapter.setUsers(filteredUsers);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadUsers() {
        allUsers = userUseCase.getAllUsers();
        userAdapter.setUsers(allUsers);
    }

    private void showUserDetails(UserDTO user) {
        String userDetails = getString(R.string.user_details,
                user.getFullName(),
                user.getEmail(),
                user.getBloodType(),
                user.getRoleName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.user_details_title)
                .setMessage(userDetails)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void handleDeleteUser(int userId) {
        if (userId == currentUser.getId()) {
            Toast.makeText(this, getString(R.string.user_deletion_restriction), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_user_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (userUseCase.deleteUser(userId)) {
                        Toast.makeText(this, getString(R.string.user_deleted), Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, getString(R.string.error_deleting_user), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void openEditUserActivity(UserDTO user) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra("userDTO", currentUser);
        intent.putExtra("user", user);
        editUserLauncher.launch(intent);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
    }

    private void initializeActivityResultLauncher() {
        editUserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        UserDTO updatedSelectedUser = Objects.requireNonNull(result.getData()).getParcelableExtra("user");
                        UserDTO updatedCurrentUser = result.getData().getParcelableExtra("userDTO");
                        if (Objects.requireNonNull(updatedSelectedUser).getId()
                                == Objects.requireNonNull(updatedCurrentUser).getId()) {
                            refreshNavHeader(updatedSelectedUser);
                        }
                        loadUsers();
                    }
                }
        );
    }
}