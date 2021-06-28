package com.example.webviewapp.common.base;

import android.app.Application;

import com.example.webviewapp.data.DataManager;

public class BaseApplication extends Application {

    private static BaseApplication applicationContext;

    public static BaseApplication getInstance() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;

        initApplication();
    }

    private void initApplication() {
        DataManager.init(getInstance());
    }

}
