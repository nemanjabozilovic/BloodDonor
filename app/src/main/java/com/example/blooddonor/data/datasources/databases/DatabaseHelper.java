package com.example.blooddonor.data.datasources.databases;

import static com.example.blooddonor.utils.DatabaseUtils.seed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blood_donor.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ROLES = "roles";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_LOCATION_TYPES = "location_types";
    private static final String TABLE_BLOOD_REQUESTS = "blood_requests";
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String TABLE_FAQ = "faq";

    // Common Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BLOOD_TYPE = "blood_type";

    // Roles Table
    private static final String COLUMN_ROLE_NAME = "role_name";

    // Users Table
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DATE_OF_BIRTH = "dateOfBirth";
    private static final String COLUMN_SALT = "salt";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture";
    private static final String COLUMN_ROLE_ID = "role_id";
    private static final String COLUMN_VERIFICATION_CODE = "verification_code";

    // Locations Table
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE_NUMBERS = "phone_numbers";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_LOCATION_TYPE_ID = "location_type_id";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";

    // Blood Requests Table
    private static final String COLUMN_SUBMITTED_BY = "submitted_by";
    private static final String COLUMN_PATIENT_NAME = "patient_name";
    private static final String COLUMN_LOCATION_ID = "location_id";
    private static final String COLUMN_POSSIBLE_DONORS = "possible_donors";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_DEADLINE = "deadline";

    // Notification Table
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_CREATED_DATE = "created_date";

    // FAQ Table
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_ANSWER = "answer";

    // Tables Creation
    private static final String CREATE_TABLE_ROLES = "CREATE TABLE " + TABLE_ROLES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ROLE_NAME + " TEXT NOT NULL UNIQUE);";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FULL_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
            COLUMN_DATE_OF_BIRTH + " TEXT NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_SALT + " TEXT NOT NULL, " +
            COLUMN_BLOOD_TYPE + " TEXT, " +
            COLUMN_PROFILE_PICTURE + " TEXT, " +
            COLUMN_ROLE_ID + " INTEGER NOT NULL, " +
            COLUMN_VERIFICATION_CODE + " INTEGER, " +
            "FOREIGN KEY (" + COLUMN_ROLE_ID + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_LOCATION_TYPES = "CREATE TABLE " + TABLE_LOCATION_TYPES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL UNIQUE);";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_PHONE_NUMBERS + " TEXT, " +
            COLUMN_LOCATION + " TEXT NOT NULL, " +
            COLUMN_LOCATION_TYPE_ID + " INTEGER NOT NULL, " +
            COLUMN_LONGITUDE + " REAL, " +
            COLUMN_LATITUDE + " REAL, " +
            "FOREIGN KEY (" + COLUMN_LOCATION_TYPE_ID + ") REFERENCES " + TABLE_LOCATION_TYPES + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_BLOOD_REQUESTS = "CREATE TABLE " + TABLE_BLOOD_REQUESTS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SUBMITTED_BY + " INTEGER NOT NULL, " +
            COLUMN_PATIENT_NAME + " TEXT NOT NULL, " +
            COLUMN_LOCATION_ID + " INTEGER NOT NULL, " +
            COLUMN_BLOOD_TYPE + " TEXT NOT NULL, " +
            COLUMN_POSSIBLE_DONORS + " TEXT NOT NULL, " +
            COLUMN_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_DEADLINE + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_SUBMITTED_BY + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES " + TABLE_LOCATIONS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_TEXT + " TEXT NOT NULL, " +
            COLUMN_CREATED_DATE + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_FAQ = "CREATE TABLE " + TABLE_FAQ + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_QUESTION + " TEXT NOT NULL, " +
            COLUMN_ANSWER + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ROLES);
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATION_TYPES);
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_BLOOD_REQUESTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_FAQ);

        seed(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAQ);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOOD_REQUESTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TYPES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        onCreate(sqLiteDatabase);
    }
}