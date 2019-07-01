package com.trigger.snha.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trigger.snha.dto.Joke;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Cupboard helper class
 * Cupboard is an ORM which provides comfortable storing/fetching of data with DB using rxJava
 */
public class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SNHA.db";
    private static final int DATABASE_VERSION = 1;

    static {
        cupboard().register(Joke.class);
    }

    public CupboardSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
