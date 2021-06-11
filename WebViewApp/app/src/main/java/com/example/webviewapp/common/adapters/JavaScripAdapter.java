package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.content.Intent;

import com.example.webviewapp.ui.activity.PictureViewActivity;

public class JavaScripAdapter {
    private final Context context;
    private final String[] imageUrls;

    public JavaScripAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }


    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent(context, PictureViewActivity.class);
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("curImageUrl", img);
        context.startActivity(intent);
    }
}
