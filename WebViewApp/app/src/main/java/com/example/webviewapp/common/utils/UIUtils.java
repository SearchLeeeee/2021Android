package com.example.webviewapp.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class UIUtils {

    public static Intent registerActivitySwitch(Activity activity,
                                                View view, Class<? extends Activity> clazz) {
        Intent intent = new Intent(activity, clazz);
        view.setOnClickListener(v -> {
            activity.startActivity(intent);
        });
        return intent;
    }

}
