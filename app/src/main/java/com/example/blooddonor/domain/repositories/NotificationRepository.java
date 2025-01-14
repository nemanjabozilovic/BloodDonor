package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.Notification;

import java.util.List;

public interface NotificationRepository {
    Notification getNotificationById(int id);
    List<Notification> getAllNotifications();
    boolean insertNotification(Notification notification);
    boolean updateNotification(Notification notification);
    boolean deleteNotification(int id);
}