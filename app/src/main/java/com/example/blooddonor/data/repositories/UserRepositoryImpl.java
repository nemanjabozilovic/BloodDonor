package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.User;
import com.example.blooddonor.domain.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BLOOD_TYPE = "blood_type";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture";
    private static final String COLUMN_ROLE_ID = "role_id";

    private final DatabaseHelper dbHelper;

    public UserRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public User getUserById(int userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            User user = mapCursorToUser(cursor);
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        while (cursor.moveToNext()) {
            users.add(mapCursorToUser(cursor));
        }
        cursor.close();
        return users;
    }

    @Override
    public boolean insertUser(User user) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.insert(TABLE_USERS, null, mapUserToContentValues(user)) != -1;
    }

    @Override
    public boolean updateUser(User user) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.update(TABLE_USERS, mapUserToContentValues(user), COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())}) > 0;
    }

    @Override
    public boolean deleteUser(int userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_USERS, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    @Override
    public boolean assignRoleToUser(int userId, int roleId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE_ID, roleId);
        return database.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    private User mapCursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID))
        );
    }

    private ContentValues mapUserToContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_BLOOD_TYPE, user.getBloodType());
        values.put(COLUMN_PROFILE_PICTURE, user.getProfilePicture());
        values.put(COLUMN_ROLE_ID, user.getRoleId());
        return values;
    }
}