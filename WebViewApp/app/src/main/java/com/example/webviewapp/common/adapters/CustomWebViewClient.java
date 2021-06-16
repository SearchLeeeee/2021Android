package com.example.webviewapp.common.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 监听webview
 */
public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = "CustomWebViewClient";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageStarted(view, url, favicon);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPageFinished(WebView view, String url) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageFinished(view, url);

        initUrl(view);
    }

    private void initUrl(WebView view) {
        //TODO:获取img的url
        //通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
        view.loadUrl("javascript:imageCall()");
        Log.d(TAG, "initUrl: urls");
        view.loadUrl("javascript:videoCall()");
    }
}
