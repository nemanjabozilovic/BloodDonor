package com.example.blooddonor;

import android.app.Application;

import androidx.work.WorkManager;

import com.example.blooddonor.workers.BirthdayNotificationWorker;
import com.example.blooddonor.workers.BloodRequestReminderWorker;

import java.util.concurrent.TimeUnit;

public class BloodDonorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        scheduleBloodRequestReminderWorker();
        scheduleBirthdayNotifications();

        //testRunWorkManager();
    }

    private void scheduleBloodRequestReminderWorker() {
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "BloodRequestReminderWorker",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                new androidx.work.PeriodicWorkRequest.Builder(BloodRequestReminderWorker.class, 1, TimeUnit.DAYS).build()
        );
    }

    private void scheduleBirthdayNotifications() {
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "BirthdayNotificationWorker",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                new androidx.work.PeriodicWorkRequest.Builder(BirthdayNotificationWorker.class, 1, TimeUnit.DAYS).build()
        );
    }

    /*private void testRunWorkManager() {
        WorkManager.getInstance(this).enqueue(
                new OneTimeWorkRequest.Builder(BirthdayNotificationWorker.class).build()
                *//*new OneTimeWorkRequest.Builder(BloodRequestReminderWorker.class).build()*//*
        );
    }*/
}