package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.Location;
import com.example.blooddonor.domain.repositories.LocationRepository;

import java.util.ArrayList;
import java.util.List;

public class LocationRepositoryImpl implements LocationRepository {
    private static final String TABLE_LOCATIONS = "locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE_NUMBERS = "phone_numbers";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_LOCATION_TYPE_ID = "location_type_id";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";

    private final DatabaseHelper dbHelper;

    public LocationRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Location getLocationById(int locationId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(locationId)});
        if (cursor.moveToFirst()) {
            Location location = mapCursorToLocation(cursor);
            cursor.close();
            return location;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LOCATIONS, null);
        while (cursor.moveToNext()) {
            locations.add(mapCursorToLocation(cursor));
        }
        cursor.close();
        return locations;
    }

    @Override
    public boolean insertLocation(Location location) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.insert(TABLE_LOCATIONS, null, mapLocationToContentValues(location)) != -1;
    }

    @Override
    public boolean updateLocation(Location location) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.update(TABLE_LOCATIONS, mapLocationToContentValues(location), COLUMN_ID + " = ?", new String[]{String.valueOf(location.getId())}) > 0;
    }

    @Override
    public boolean deleteLocation(int locationId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_LOCATIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(locationId)}) > 0;
    }

    private Location mapCursorToLocation(Cursor cursor) {
        return new Location(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBERS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_TYPE_ID)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
        );
    }

    private ContentValues mapLocationToContentValues(Location location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, location.getName());
        values.put(COLUMN_PHONE_NUMBERS, location.getPhoneNumbers());
        values.put(COLUMN_LOCATION, location.getLocation());
        values.put(COLUMN_LOCATION_TYPE_ID, location.getLocationTypeId());
        values.put(COLUMN_LONGITUDE, location.getLongitude());
        values.put(COLUMN_LATITUDE, location.getLatitude());
        return values;
    }
}