package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.Faq;
import com.example.blooddonor.domain.repositories.FaqRepository;

import java.util.ArrayList;
import java.util.List;

public class FaqRepositoryImpl implements FaqRepository {
    private static final String TABLE_FAQ = "faq";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_ANSWER = "answer";

    private final DatabaseHelper dbHelper;

    public FaqRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Faq getFaqById(int faqId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_FAQ + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(faqId)});
        if (cursor.moveToFirst()) {
            Faq faq = new Faq(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))
            );
            cursor.close();
            return faq;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<Faq> getAllFaqs() {
        List<Faq> faqs = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_FAQ, null);
        while (cursor.moveToNext()) {
            faqs.add(new Faq(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))
            ));
        }
        cursor.close();
        return faqs;
    }

    @Override
    public boolean insertFaq(Faq faq) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, faq.getQuestion());
        values.put(COLUMN_ANSWER, faq.getAnswer());
        return database.insert(TABLE_FAQ, null, values) != -1;
    }

    @Override
    public boolean updateFaq(Faq faq) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, faq.getQuestion());
        values.put(COLUMN_ANSWER, faq.getAnswer());
        return database.update(TABLE_FAQ, values, COLUMN_ID + " = ?", new String[]{String.valueOf(faq.getId())}) > 0;
    }

    @Override
    public boolean deleteFaq(int faqId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_FAQ, COLUMN_ID + " = ?", new String[]{String.valueOf(faqId)}) > 0;
    }
}