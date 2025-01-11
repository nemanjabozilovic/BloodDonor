package com.example.blooddonor.data.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blooddonor.data.datasources.databases.DatabaseHelper;
import com.example.blooddonor.data.models.NewsAndTips;
import com.example.blooddonor.domain.repositories.NewsAndTipsRepository;

import java.util.ArrayList;
import java.util.List;

public class NewsAndTipsRepositoryImpl implements NewsAndTipsRepository {
    private static final String TABLE_NEWS_AND_TIPS = "news_and_tips";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_CREATED_DATE = "created_date";

    private final DatabaseHelper dbHelper;

    public NewsAndTipsRepositoryImpl(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public NewsAndTips getNewsAndTipsById(int id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NEWS_AND_TIPS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            NewsAndTips newsAndTips = mapCursorToNewsAndTips(cursor);
            cursor.close();
            return newsAndTips;
        }
        cursor.close();
        return null;
    }

    @Override
    public List<NewsAndTips> getAllNewsAndTips() {
        List<NewsAndTips> newsAndTipsList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NEWS_AND_TIPS, null);
        while (cursor.moveToNext()) {
            newsAndTipsList.add(mapCursorToNewsAndTips(cursor));
        }
        cursor.close();
        return newsAndTipsList;
    }

    @Override
    public boolean insertNewsAndTips(NewsAndTips newsAndTips) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = mapNewsAndTipsToContentValues(newsAndTips);
        return database.insert(TABLE_NEWS_AND_TIPS, null, values) != -1;
    }

    @Override
    public boolean updateNewsAndTips(NewsAndTips newsAndTips) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = mapNewsAndTipsToContentValues(newsAndTips);
        return database.update(TABLE_NEWS_AND_TIPS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(newsAndTips.getId())}) > 0;
    }

    @Override
    public boolean deleteNewsAndTips(int id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_NEWS_AND_TIPS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    private NewsAndTips mapCursorToNewsAndTips(Cursor cursor) {
        return new NewsAndTips(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE))
        );
    }

    private ContentValues mapNewsAndTipsToContentValues(NewsAndTips newsAndTips) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newsAndTips.getTitle());
        values.put(COLUMN_TEXT, newsAndTips.getText());
        values.put(COLUMN_CREATED_DATE, newsAndTips.getCreatedDate());
        return values;
    }
}