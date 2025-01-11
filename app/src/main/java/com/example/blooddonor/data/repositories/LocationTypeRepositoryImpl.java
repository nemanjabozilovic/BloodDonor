package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.LocationType;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;

import java.util.ArrayList;
import java.util.List;

public class LocationTypeRepositoryImpl implements LocationTypeRepository {
    private static final String TABLE_LOCATION_TYPES = "location_types";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private final DatabaseHelper dbHelper;

    public LocationTypeRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public LocationType getLocationTypeById(int typeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LOCATION_TYPES + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(typeId)});
        if (cursor.moveToFirst()) {
            LocationType locationType = new LocationType(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            );
            cursor.close();
            return locationType;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<LocationType> getAllLocationTypes() {
        List<LocationType> locationTypes = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LOCATION_TYPES, null);
        while (cursor.moveToNext()) {
            locationTypes.add(new LocationType(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            ));
        }
        cursor.close();
        return locationTypes;
    }

    @Override
    public boolean insertLocationType(LocationType locationType) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, locationType.getName());
        return database.insert(TABLE_LOCATION_TYPES, null, values) != -1;
    }

    @Override
    public boolean updateLocationType(LocationType locationType) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, locationType.getName());
        return database.update(TABLE_LOCATION_TYPES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(locationType.getId())}) > 0;
    }

    @Override
    public boolean deleteLocationType(int typeId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_LOCATION_TYPES, COLUMN_ID + " = ?", new String[]{String.valueOf(typeId)}) > 0;
    }
}