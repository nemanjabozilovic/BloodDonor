package com.example.blooddonor.ui.activities;

import static com.example.blooddonor.utils.TextInputHelper.addTextWatcher;
import static com.example.blooddonor.utils.TextInputHelper.clearError;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonor.R;
import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.BloodRequestUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.RoleUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.ui.adapters.BloodRequestAdapter;
import com.example.blooddonor.utils.EmailValidator;
import com.example.blooddonor.utils.ImageUploadHelper;
import com.example.blooddonor.utils.PasswordUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends BaseActivity {

    private BloodRequestAdapter bloodRequestAdapter;
    private BloodRequestUseCase bloodRequestUseCase;
    private RoleUseCase roleUseCase;
    private UserUseCase userUseCase;

    private ImageView profileImage;
    private TextView userName, userEmail, noBloodRequestsText, bloodRequestTitle;
    private MaterialButton createBloodRequestButton;
    private RecyclerView bloodRequestsRecyclerView;


    private UserDTO currentUser;

    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.content_frame));
        setActiveMenuItem(R.id.nav_profile);
        setTitle(R.string.profile);
        initializeDependencies();
        initializeCurrentUser();
        initializeUIElements();
        setupImagePickerLauncher();
        loadUserData();
        loadBloodRequests();
    }

    private void initializeDependencies() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        bloodRequestUseCase = new BloodRequestUseCaseImpl(bloodRequestRepository, locationRepository);
        roleUseCase = new RoleUseCaseImpl(roleRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
    }

    private void initializeCurrentUser() {
        currentUser = getIntent().getParcelableExtra("userDTO");
        if (currentUser == null) {
            Toast.makeText(this, R.string.no_user_data_found, Toast.LENGTH_SHORT).show();
        } else {
            currentUser.setRoleName(roleUseCase.getRoleById(currentUser.getRoleId()).getRoleName());
        }
    }

    private void initializeUIElements() {
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        bloodRequestsRecyclerView = findViewById(R.id.blood_requests_recycler_view);
        bloodRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bloodRequestAdapter = new BloodRequestAdapter(new ArrayList<>());
        bloodRequestsRecyclerView.setAdapter(bloodRequestAdapter);
        noBloodRequestsText = findViewById(R.id.no_blood_requests_text);
        createBloodRequestButton = findViewById(R.id.create_blood_request_button);
        bloodRequestTitle = findViewById(R.id.blood_request_title);
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Glide.with(profileImage.getContext()).load(selectedImageUri).placeholder(R.drawable.ic_profile_placeholder).into(profileImage);
                        Toast.makeText(this, R.string.profile_image_saved, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        List<BloodRequestDTO> createdBloodRequests = bloodRequestUseCase.getBloodRequestsByUserId(currentUser.getId());

        if (createdBloodRequests != null && !createdBloodRequests.isEmpty()) {
            bloodRequestTitle.setVisibility(View.VISIBLE);
            bloodRequestsRecyclerView.setVisibility(View.VISIBLE);
            noBloodRequestsText.setVisibility(View.GONE);
            createBloodRequestButton.setVisibility(View.GONE);

            bloodRequestAdapter.updateData(createdBloodRequests);
        } else {
            bloodRequestTitle.setVisibility(View.GONE);
            bloodRequestsRecyclerView.setVisibility(View.GONE);
            noBloodRequestsText.setVisibility(View.VISIBLE);
            createBloodRequestButton.setVisibility(View.VISIBLE);
        }

        createBloodRequestButton.setOnClickListener(v -> openRequestBloodActivity());
    }

    private void openRequestBloodActivity() {
        Intent intent = new Intent(this, RequestBloodActivity.class);
        intent.putExtra("userDTO", currentUser);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            openEditProfileDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_profile);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        ImageView profileImageView = dialogView.findViewById(R.id.profile_image);
        TextInputEditText fullNameField = dialogView.findViewById(R.id.full_name);
        TextInputEditText emailField = dialogView.findViewById(R.id.email);
        TextInputEditText dateOfBirthField = dialogView.findViewById(R.id.date_of_birth);
        TextInputLayout fullNameInputLayout = dialogView.findViewById(R.id.full_name_input_layout);
        TextInputLayout emailInputLayout = dialogView.findViewById(R.id.email_input_layout);
        TextInputLayout dateOfBirthInputLayout = dialogView.findViewById(R.id.date_of_birth_input_layout);
        AutoCompleteTextView bloodTypeDropdown = dialogView.findViewById(R.id.blood_type_dropdown);
        MaterialButton changePasswordButton = dialogView.findViewById(R.id.change_password_button);
        fullNameField.setText(currentUser.getFullName());
        emailField.setText(currentUser.getEmail());
        dateOfBirthField.setText(currentUser.getDateOfBirth());
        bloodTypeDropdown.setText(currentUser.getBloodType(), false);

        addTextWatcher(fullNameField, fullNameInputLayout);
        addTextWatcher(emailField, emailInputLayout);
        addTextWatcher(dateOfBirthField, dateOfBirthInputLayout);

        Glide.with(this).load(currentUser.getProfilePicture()).placeholder(R.drawable.ic_profile_placeholder).into(profileImageView);

        populateBloodTypeDropdown(bloodTypeDropdown);

        changePasswordButton.setOnClickListener(v -> openChangePasswordDialog());
        dateOfBirthField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(v.getContext(), (datePicker, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                dateOfBirthField.setText(formattedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        profileImageView.setOnClickListener(v ->
                imagePickerLauncher.launch(
                        new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build()
                )
        );

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String fullName = Objects.requireNonNull(fullNameField.getText()).toString().trim();
            String email = Objects.requireNonNull(emailField.getText()).toString().trim();
            String dateOfBirth = Objects.requireNonNull(dateOfBirthField.getText()).toString().trim();

            clearError(fullNameInputLayout);
            clearError(emailInputLayout);
            clearError(dateOfBirthInputLayout);

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(dateOfBirth)) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!EmailValidator.isValidEmail(email)) {
                Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setDateOfBirth(dateOfBirth);
            currentUser.setRoleName(currentUser.getRoleName());
            currentUser.setBloodType(bloodTypeDropdown.getText().toString());

            if (selectedImageUri != null) {
                try {
                    String savedPath = ImageUploadHelper.saveImageToAppStorage(this, selectedImageUri, currentUser.getProfilePicture());
                    currentUser.setProfilePicture(savedPath);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.error_saving_image, Toast.LENGTH_SHORT).show();
                }
            }

            if (userUseCase.updateUser(currentUser) != null) {
                Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                loadUserData();
                refreshNavHeader(currentUser);
            } else {
                Toast.makeText(this, R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_password);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextInputEditText newPasswordField = dialogView.findViewById(R.id.new_password_field);
        TextInputEditText confirmPasswordField = dialogView.findViewById(R.id.confirm_password_field);
        TextInputLayout newPasswordInputLayout = dialogView.findViewById(R.id.new_password_input_layout);
        TextInputLayout confirmPasswordInputLayout = dialogView.findViewById(R.id.confirm_password_input_layout);


        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String newPassword = Objects.requireNonNull(newPasswordField.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmPasswordField.getText()).toString().trim();

            clearError(newPasswordInputLayout);
            clearError(confirmPasswordInputLayout);

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, R.string.error_passwords_not_matching, Toast.LENGTH_SHORT).show();
                return;
            }

            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(newPassword, salt);
            currentUser.setPassword(hashedPassword);
            currentUser.setSalt(salt);

            Toast.makeText(this, R.string.new_password_saved, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void populateBloodTypeDropdown(AutoCompleteTextView bloodTypeDropdown) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.blood_types));
        bloodTypeDropdown.setAdapter(adapter);
        bloodTypeDropdown.setOnClickListener(v -> bloodTypeDropdown.showDropDown());
        bloodTypeDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bloodTypeDropdown.showDropDown();
            }
        });
    }
}