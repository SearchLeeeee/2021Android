package com.example.webviewapp.common.base;

import android.app.Application;

import com.example.webviewapp.data.DataManager;

public class BaseApplication extends Application {

    private static BaseApplication applicationContext;
  //  private static Handler handler;

    public static BaseApplication getInstance() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        applicationContext = this;
        super.onCreate();

        initApplication();
    }

    private void initApplication() {
        DataManager.init(getInstance());
    }

}
