package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.Role;
import com.example.blooddonor.domain.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepository {
    private static final String TABLE_ROLES = "roles";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ROLE_NAME = "role_name";

    private final DatabaseHelper dbHelper;

    public RoleRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Role getRoleById(int roleId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ROLES + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(roleId)});
        if (cursor.moveToFirst()) {
            Role role = new Role(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE_NAME)));
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ROLES, null);
        while (cursor.moveToNext()) {
            roles.add(new Role(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE_NAME))));
        }
        cursor.close();
        return roles;
    }

    @Override
    public boolean insertRole(Role role) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE_NAME, role.getRoleName());
        return database.insert(TABLE_ROLES, null, values) != -1;
    }

    @Override
    public boolean updateRole(Role role) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE_NAME, role.getRoleName());
        return database.update(TABLE_ROLES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(role.getId())}) > 0;
    }

    @Override
    public boolean deleteRole(int roleId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_ROLES, COLUMN_ID + " = ?", new String[]{String.valueOf(roleId)}) > 0;
    }
}