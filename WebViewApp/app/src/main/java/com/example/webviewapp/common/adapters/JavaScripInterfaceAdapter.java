package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.webviewapp.ui.activity.PictureViewActivity;
import com.example.webviewapp.ui.activity.VideoViewActivity;

/**
 * 实现js调用本地方法
 */
public class JavaScripInterfaceAdapter {
    private static final String TAG = "JavaScripAdapter";

    private final Context context;
    private String[] imageUrls;

    public JavaScripInterfaceAdapter(Context context) {
        this.context = context;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    @JavascriptInterface
    public void openImage(String img, String[] urls) {
        Log.d(TAG, "openImage: urls" + img);
        Intent intent = new Intent(context, PictureViewActivity.class);
        intent.putExtra("imageUrls", urls);
        intent.putExtra("curImageUrl", img);

        context.startActivity(intent);
    }

    @JavascriptInterface
    public void openVideo(String video) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra("video", video);
        Log.d(TAG, "openImage: urls" + video);
        context.startActivity(intent);
    }

}
