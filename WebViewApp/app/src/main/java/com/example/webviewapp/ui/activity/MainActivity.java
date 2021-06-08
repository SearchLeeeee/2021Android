package com.example.webviewapp.ui.activity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainpage();
    }

    public void mainpage() {
        WebView myWebView = findViewById(R.id.webview);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton refreshButton = findViewById(R.id.refreshButton);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton forwardButton = findViewById(R.id.fowardButton);
        myWebView.loadUrl("http://www.baidu.com");
        myWebView.setWebViewClient(new WebViewClient() {
            //在webview里打开新链接
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        //初始化菜单栏


        initButton(myWebView, menuButton, refreshButton, backButton, forwardButton);


    }

    private void initButton(WebView myWebView, ImageButton menuButton, ImageButton refreshButton, ImageButton backButton, ImageButton forwardButton) {
        //设置按钮的点击事件
        menuButton.setOnClickListener(v -> {
            Log.d("TAG", "菜单栏点击");
            popwindow();
        });

        refreshButton.setOnClickListener(v -> {
            Log.d("TAG", "onClick:refresh");
            myWebView.clearCache(true);
            myWebView.reload();
        });

        backButton.setOnClickListener(v -> {
            if (myWebView.canGoBack()) myWebView.goBack();
            else myWebView.goBack();
            //返回键还是好做啊
            Log.d("TAG", "mainpage: backward ");
        });

        forwardButton.setOnClickListener(v -> {
            if (myWebView.canGoForward())
                myWebView.goForward();
            //是前进捏

        });
    }

    //脚手架（菜单栏的实现）：
    //浮动窗口实现
    public void popwindow() {
        // PopWindow 布局发
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_mainpage, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor((v, event) -> {
            Log.d("TAG", "onTouch: popwindowss");
            return false;
        });
        popWindow.showAtLocation(view, 80, 0, 0);
    }


    // 弹出窗实现
    public void alertdialog() {
        //   AlertDialog布局法：
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // builder = new AlertDialog.Builder(getApplicationContext());
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view_menu = inflater.inflate(R.layout.menu_mainpage, null, false);
        builder.setView(view_menu);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}

