package com.trigger.snha;

import android.app.Application;

import com.trigger.snha.model.GlobalRepository;

public class SnApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialising the global repository object and providing the context
        GlobalRepository.getInstance().setCtx(getApplicationContext());
    }
}
