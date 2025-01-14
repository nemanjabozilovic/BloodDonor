package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.Notification;
import com.example.blooddonor.domain.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_CREATED_DATE = "created_date";

    private final DatabaseHelper dbHelper;

    public NotificationRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Notification getNotificationById(int id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Notification notification = mapCursorToNewsAndTips(cursor);
            cursor.close();
            return notification;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<Notification> getAllNotifications() {
        List<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION, null);
        while (cursor.moveToNext()) {
            notificationList.add(mapCursorToNewsAndTips(cursor));
        }
        cursor.close();
        return notificationList;
    }

    @Override
    public boolean insertNotification(Notification notification) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = mapNewsAndTipsToContentValues(notification);
        return database.insert(TABLE_NOTIFICATION, null, values) != -1;
    }

    @Override
    public boolean updateNotification(Notification notification) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = mapNewsAndTipsToContentValues(notification);
        return database.update(TABLE_NOTIFICATION, values, COLUMN_ID + " = ?", new String[]{String.valueOf(notification.getId())}) > 0;
    }

    @Override
    public boolean deleteNotification(int id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_NOTIFICATION, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    private Notification mapCursorToNewsAndTips(Cursor cursor) {
        return new Notification(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE))
        );
    }

    private ContentValues mapNewsAndTipsToContentValues(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, notification.getTitle());
        values.put(COLUMN_TEXT, notification.getText());
        values.put(COLUMN_CREATED_DATE, notification.getCreatedDate());
        return values;
    }
}