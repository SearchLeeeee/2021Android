package com.example.webviewapp.common.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.webviewapp.contract.MainContract;
import com.example.webviewapp.presenter.MainPresenter;


/**
 * 监听webview
 */
public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = "CustomWebViewClient";
    MainContract.Presenter presenter = new MainPresenter();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageStarted(view, url, favicon);
        String title = view.getTitle();
        presenter.addHistory(url, title);
        Log.d(TAG, "onPageStarted: 页面初始化" + title);
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
        Log.d(TAG, "initUrl: urls1");
        view.loadUrl("javascript:videoCall()");
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }
}
