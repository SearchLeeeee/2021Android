package com.example.webviewapp.common.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.webviewapp.common.utils.AdBlocker;
import com.example.webviewapp.contract.MainContract;

/**
 * 监听webview
 */
public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = "CustomWebViewClient";

    public String blockUrl = "";

    private final MainContract.Presenter presenter;

    public CustomWebViewClient(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (AdBlocker.isAd(url)) {
            blockUrl = url;
            view.loadUrl("file:///android_asset/askToJump.html");
            //表示我已经处理过了
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
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
        view.loadUrl("javascript:function imageCall()\n" +
                "    {\n" +
                "        var objs = document.getElementsByTagName('img');\n" +
                "        var array=new Array();\n" +
                "        for(var j=0;j<objs.length;j++)\n" +
                "            {\n" +
                "                array[j]=objs[j].src;\n" +
                "            }\n" +
                "        for(var i=0;i<objs.length;i++)\n" +
                "            {\n" +
                "                objs[i].onclick=function()\n" +
                "                    {\n" +
                "                        window.imagelistener.openImage(this.src,array);\n" +
                "                        //javascript: imagelistener.openImage(this.src,array);\n" +
                "                        //alert('image')\n" +
                "                    }\n" +
                "            }\n" +
                "    }\n" +
                "    imageCall()");
//        view.loadUrl("javascript:imageCall()");
        Log.d(TAG, "initUrl: urls1");
        view.loadUrl("javascript:videoCall()");
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }
}
