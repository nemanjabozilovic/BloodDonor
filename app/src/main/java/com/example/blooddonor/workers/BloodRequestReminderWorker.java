package com.example.blooddonor.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.repositories.BloodRequestRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationRepositoryImpl;
import com.example.blooddonor.data.repositories.LocationTypeRepositoryImpl;
import com.example.blooddonor.data.repositories.RoleRepositoryImpl;
import com.example.blooddonor.data.repositories.UserRepositoryImpl;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.implementation.BloodRequestUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.LocationUseCaseImpl;
import com.example.blooddonor.domain.usecases.implementation.UserUseCaseImpl;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.EmailSender;
import com.example.blooddonor.utils.EmailTemplates;
import com.example.blooddonor.utils.NotificationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BloodRequestReminderWorker extends Worker {

    private final BloodRequestUseCase bloodRequestUseCase;
    private final UserUseCase userUseCase;
    private final LocationUseCase locationUseCase;
    private final EmailSender emailSender;

    public BloodRequestReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        BloodRequestRepository bloodRequestRepository = new BloodRequestRepositoryImpl(dbHelper);
        LocationRepository locationRepository = new LocationRepositoryImpl(dbHelper);
        LocationTypeRepository locationTypeRepository = new LocationTypeRepositoryImpl(dbHelper);
        RoleRepository roleRepository = new RoleRepositoryImpl(dbHelper);
        UserRepository userRepository = new UserRepositoryImpl(dbHelper);

        bloodRequestUseCase = new BloodRequestUseCaseImpl(bloodRequestRepository, locationRepository, userRepository);
        userUseCase = new UserUseCaseImpl(userRepository, roleRepository);
        locationUseCase = new LocationUseCaseImpl(locationRepository, locationTypeRepository, bloodRequestRepository);
        emailSender = new EmailSender("office@blooddonor.com");
    }

    @NonNull
    @Override
    public Result doWork() {
        List<BloodRequestDTO> requests = bloodRequestUseCase.getAllBloodRequests();
        long currentTime = System.currentTimeMillis();
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

        for (BloodRequestDTO request : requests) {
            Date deadline = parseDate(request.getDeadline());

            if (deadline != null && (deadline.getTime() - currentTime <= twentyFourHoursInMillis)) {
                sendDeadlineEmail(request, deadline);
                sendNotification(request);
            }
        }
        return Result.success();
    }

    private void sendDeadlineEmail(BloodRequestDTO request, Date deadline) {
        Map<String, String> allUserEmails = userUseCase.getAllUserEmails();
        String subject = EmailTemplates.getBloodRequestDeadlineSubject();

        for (Map.Entry<String, String> user : allUserEmails.entrySet()) {
            String body = EmailTemplates.getBloodRequestDeadlineTemplate(
                    user.getKey(),
                    request.getPatientName(),
                    request.getBloodType(),
                    request.getLocationName(),
                    locationUseCase.getLocationById(request.getLocationId()).getPhoneNumbers(),
                    formatDate(deadline)
            );

            emailSender.sendEmail(user.getValue(), subject, body, new EmailSender.EmailSendCallback() {
                @Override
                public void onSuccess() {
                    Log.d("BloodRequestWorker", "Email sent to: " + user.getValue());
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("BloodRequestWorker", "Error sending email: " + errorMessage);
                }
            });
        }
    }

    private void sendNotification(BloodRequestDTO request) {
        NotificationHelper.showNotification(
                getApplicationContext(),
                "⏳ Blood Donation Reminder ⏳",
                "The deadline for blood request for " + request.getPatientName() + " at " + request.getLocationName() + " is approaching."
        );
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }
}