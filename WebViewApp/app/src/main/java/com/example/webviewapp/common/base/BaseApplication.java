package com.example.webviewapp.common.base;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.webviewapp.common.utils.AdBlocker;
import com.example.webviewapp.data.DataManager;

public class BaseApplication extends Application {

    private static BaseApplication applicationContext;

    public static BaseApplication getInstance() {
        return applicationContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;

        initApplication();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initApplication() {
        DataManager.init(getInstance());
        AdBlocker.init(getInstance());
    }

}
