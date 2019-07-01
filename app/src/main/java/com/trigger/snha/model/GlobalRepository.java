package com.trigger.snha.model;

import android.content.Context;

import com.trigger.snha.db.CupboardSQLiteOpenHelper;

/**
 * Holds all the neccessary objects and data
 */
public class GlobalRepository {
    Context ctx;

    UserRepository userRepository;
    JokesRepository jokesRepository;

    CupboardSQLiteOpenHelper dbHelper;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public JokesRepository getJokesRepository() {
        return jokesRepository;
    }

    private static final GlobalRepository instance = new GlobalRepository();

    public static GlobalRepository getInstance() {
        return instance;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;

        initContextDependent();
    }

    private void initContextDependent() {
        dbHelper = new CupboardSQLiteOpenHelper(ctx);
        userRepository = new UserRepository();
        jokesRepository = new JokesRepository();
    }

    public CupboardSQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    private GlobalRepository() {

    }
}
