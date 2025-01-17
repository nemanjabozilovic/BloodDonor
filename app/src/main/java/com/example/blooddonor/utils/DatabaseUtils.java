package com.example.blooddonor.utils;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {

    public static void seed(SQLiteDatabase sqLiteDatabase) {
        insertRoles(sqLiteDatabase);
        insertLocationTypes(sqLiteDatabase);
        insertAdminUser(sqLiteDatabase);
        insertFaqs(sqLiteDatabase);
    }

    private static void insertRoles(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("INSERT INTO roles (role_name) VALUES ('Admin'), ('Natural Person'), ('Legal Entity');");
    }

    private static void insertLocationTypes(SQLiteDatabase db) {
        db.execSQL("INSERT INTO location_types (name) VALUES ('Hospital'), ('Health Center'), ('Medical Faculty');");
    }

    private static void insertAdminUser(SQLiteDatabase sqLiteDatabase) {
        String adminFullName = "Admin";
        String adminEmail = "admin@blooddonor.com";
        String adminSalt = PasswordUtils.generateSalt();
        String adminPasswordHash = PasswordUtils.hashPassword("cGFzc3dvcmQ=", adminSalt);
        String adminDateOfBirth = "1998-05-23";

        sqLiteDatabase.execSQL("INSERT INTO users (full_name, email, password, salt, role_id, blood_type, dateOfBirth) " +
                "VALUES ('" + adminFullName + "', '" + adminEmail + "', '" + adminPasswordHash + "', '" + adminSalt + "', " +
                "(SELECT id FROM roles WHERE role_name = 'Admin'), '0+', '" + adminDateOfBirth + "');");
    }

    private static void insertFaqs(SQLiteDatabase sqLiteDatabase) {
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
            sqLiteDatabase.execSQL("INSERT INTO faq (question, answer) VALUES (?, ?);", new Object[]{faq[0], faq[1]});
        }
    }
}
