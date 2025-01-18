package com.example.blooddonor.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.EmailSender;
import com.example.blooddonor.utils.EmailTemplates;
import com.example.blooddonor.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BirthdayNotificationWorker extends Worker {

    private final UserUseCase userUseCase;
    private final EmailSender emailSender;

    public BirthdayNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
        emailSender = new EmailSender("office@blooddonor.com");
    }

    @NonNull
    @Override
    public Result doWork() {
        List<UserDTO> users = userUseCase.getAllUsers();

        for (UserDTO user : users) {
            if (!isAdmin(user) && isBirthdayToday(user.getDateOfBirth())) {
                sendBirthdayNotification(user);
                sendBirthdayEmail(user);
            }
        }
        return Result.success();
    }

    private boolean isAdmin(UserDTO user) {
        return user.getRoleName() != null && user.getRoleName().equalsIgnoreCase("Admin");
    }

    private void sendBirthdayNotification(UserDTO user) {
        NotificationHelper.showNotification(
                getApplicationContext(),
                "🎉 Happy Birthday " + user.getFullName() + "!",
                "Wishing you a fantastic day filled with joy and happiness. 🎂"
        );
    }

    private void sendBirthdayEmail(UserDTO user) {
        String subject = EmailTemplates.getBirthdaySubject();
        String body = EmailTemplates.getBirthdayTemplate(user.getFullName());

        emailSender.sendEmail(user.getEmail(), subject, body, new EmailSender.EmailSendCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private boolean isBirthdayToday(String dateOfBirth) {
        try {
            Date birthDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateOfBirth);
            String birthDateFormatted = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(Objects.requireNonNull(birthDate));
            return birthDateFormatted.equals(getCurrentDate());
        } catch (Exception e) {
            return false;
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date());
    }
}
