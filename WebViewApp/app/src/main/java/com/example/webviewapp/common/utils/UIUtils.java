package com.example.webviewapp.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class UIUtils {

    /**
     * 封装好点击一个view跳转到一个activity的动作
     * @param activity 当前context
     * @param view 被点击的view
     * @param clazz 目标activity
     * @return
     */
    public static Intent registerActivitySwitch(Activity activity,
                                                View view, Class<? extends Activity> clazz) {
        Intent intent = new Intent(activity, clazz);
        view.setOnClickListener(v -> {
            activity.startActivity(intent);
        });
        return intent;
    }

}
