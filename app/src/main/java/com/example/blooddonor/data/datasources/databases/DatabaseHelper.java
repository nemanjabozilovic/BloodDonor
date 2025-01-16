package com.example.blooddonor.data.datasources.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.blooddonor.utils.PasswordUtils;

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
            "FOREIGN KEY (" + COLUMN_SUBMITTED_BY + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES " + TABLE_LOCATIONS + "(" + COLUMN_ID + "));";

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
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROLES);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_LOCATION_TYPES);
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_BLOOD_REQUESTS);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_FAQ);

        seed(db);
    }

    private static void seed(SQLiteDatabase db) {
        // Roles
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_NAME + ") VALUES ('Admin'), ('Natural Person'), ('Legal Entity');");

        // Location Types
        db.execSQL("INSERT INTO " + TABLE_LOCATION_TYPES + " (" + COLUMN_NAME + ") VALUES ('Hospital'), ('Health Center'), ('Medical Faculty');");

        // Admin User
        String adminFullName = "John Doe";
        String adminEmail = "john.doe@blooddonor.com";
        String adminSalt = PasswordUtils.generateSalt();
        String adminPasswordHash = PasswordUtils.hashPassword("cGFzc3dvcmQ=", adminSalt);
        String adminDateOfBirth = "1998-05-23";

        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                COLUMN_FULL_NAME + ", " +
                COLUMN_EMAIL + ", " +
                COLUMN_PASSWORD + ", " +
                COLUMN_SALT + ", " +
                COLUMN_ROLE_ID + ", " +
                COLUMN_BLOOD_TYPE + ", " +
                COLUMN_DATE_OF_BIRTH + ") " +
                "VALUES ('" + adminFullName + "', '" +
                adminEmail + "', '" +
                adminPasswordHash + "', '" +
                adminSalt + "', " +
                "(SELECT " + COLUMN_ID + " FROM " + TABLE_ROLES + " WHERE " + COLUMN_ROLE_NAME + " = 'Admin'), " +
                "'0+', '" + adminDateOfBirth + "');");

        String[][] faqs = {
                {"Do I always need to fill out the blood donor questionnaire?", "Yes, before each blood donation, it is necessary to complete and sign the blood donor questionnaire."},
                {"Will my blood be tested?", "Every blood donation is tested for:\n– Blood type determination in the ABO and Rh(D) system, and screening for irregular antibodies\n– Screening for markers of Hepatitis B, Hepatitis C, HIV/AIDS, and Syphilis"},
                {"Why is hemoglobin level checked before donating blood?", "Before donation, each donor's hemoglobin level is checked. Only individuals who meet the required criteria can donate blood. This prevents iron deficiency in our donors."},
                {"Which blood type is most needed?", "Yours!"},
                {"How long does blood donation take?", "The entire procedure, including filling out the questionnaire, medical and hematological examination, and donation, takes about 20 minutes."},
                {"Do I need to rest the arm from which blood was taken, and for how long?", "The bandage should be worn on the puncture site for at least two hours after donation, and intense use of the arm should be avoided for at least 12 hours."},
                {"Should I be mindful of what I eat and drink after donation?", "After donating blood, it is advisable to stay hydrated with water or non-alcoholic beverages for the next 24 hours and have regular meals."},
                {"Should I eat something before donating blood?", "A light carbohydrate meal is recommended."},
                {"Can I go straight home after donating blood?", "After donating blood, donors can return to their usual activities while avoiding risky behaviors (smoking, excessive physical activity, alcohol consumption) for the next 24 hours."},
                {"Why is it good to stay hydrated before donating blood?", "To better prepare the body for the temporary loss of a certain amount of fluid."},
                {"How much blood is taken during donation?", "The standard amount of donated blood is always the same: 450ml."},
                {"How often can I donate blood?", "Women can donate blood every 16 weeks, while men can donate every 12 weeks."},
                {"Is my blood type really needed?", "All blood types are equally needed and important."},
                {"I don't know how many times I have donated blood. Can I check?", "Donation records are always available and easily verifiable in our information system."},
                {"What about privacy during my visit to the blood donation center?", "All donor data is used exclusively for transfusion service purposes, and any other misuse is strictly prohibited."},
                {"Does blood donation affect blood pressure?", "Blood donation has a beneficial effect on people with hypertension but may cause temporary blood pressure complications in individuals with very low arterial pressure."},
                {"Are children or friends allowed to accompany me while I donate blood?", "Of course, if that is your wish."},
                {"I got a bruise after donating blood. Is that normal?", "Sometimes, due to damage to the blood vessel wall at the puncture site, bruises may appear. It is best to treat them with cold compresses. These bruises are temporary and usually disappear after a few days."},
                {"What kind of clothing should I wear when donating blood?", "Clothing should be appropriate for the weather conditions, and it is advisable to avoid tight sleeves for easier medical examination and donation."}
        };

        for (String[] faq : faqs) {
            db.execSQL("INSERT INTO " + TABLE_FAQ + " (" + COLUMN_QUESTION + ", " + COLUMN_ANSWER + ") VALUES (?, ?);",
                    new Object[]{faq[0], faq[1]});
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAQ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOOD_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        onCreate(db);
    }
}