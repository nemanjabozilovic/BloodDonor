package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestRepositoryImpl implements BloodRequestRepository {
    private static final String TABLE_BLOOD_REQUESTS = "blood_requests";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBMITTED_BY = "submitted_by";
    private static final String COLUMN_PATIENT_NAME = "patient_name";
    private static final String COLUMN_LOCATION_ID = "location_id";
    private static final String COLUMN_BLOOD_TYPE = "blood_type";
    private static final String COLUMN_POSSIBLE_DONORS = "possible_donors";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_DEADLINE = "deadline";

    private final DatabaseHelper dbHelper;

    public BloodRequestRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public BloodRequest getBloodRequestById(int requestId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_BLOOD_REQUESTS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(requestId)});
        if (cursor.moveToFirst()) {
            BloodRequest request = mapCursorToBloodRequest(cursor);
            cursor.close();
            return request;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<BloodRequest> getAllBloodRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_BLOOD_REQUESTS, null);
        while (cursor.moveToNext()) {
            requests.add(mapCursorToBloodRequest(cursor));
        }
        cursor.close();
        return requests;
    }

    @Override
    public boolean insertBloodRequest(BloodRequest request) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.insert(TABLE_BLOOD_REQUESTS, null, mapBloodRequestToContentValues(request)) != -1;
    }

    @Override
    public boolean updateBloodRequest(BloodRequest request) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.update(TABLE_BLOOD_REQUESTS, mapBloodRequestToContentValues(request), COLUMN_ID + " = ?", new String[]{String.valueOf(request.getId())}) > 0;
    }

    @Override
    public boolean deleteBloodRequest(int requestId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_BLOOD_REQUESTS, COLUMN_ID + " = ?", new String[]{String.valueOf(requestId)}) > 0;
    }

    private BloodRequest mapCursorToBloodRequest(Cursor cursor) {
        return new BloodRequest(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMITTED_BY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSSIBLE_DONORS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
        );
    }

    private ContentValues mapBloodRequestToContentValues(BloodRequest request) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBMITTED_BY, request.getSubmittedBy());
        values.put(COLUMN_PATIENT_NAME, request.getPatientName());
        values.put(COLUMN_LOCATION_ID, request.getLocationId());
        values.put(COLUMN_BLOOD_TYPE, request.getBloodType());
        values.put(COLUMN_POSSIBLE_DONORS, request.getPossibleDonors());
        values.put(COLUMN_CREATED_AT, request.getCreatedAt());
        values.put(COLUMN_DEADLINE, request.getDeadline());
        return values;
    }
}