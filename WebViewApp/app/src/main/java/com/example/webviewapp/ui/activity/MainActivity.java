package com.example.webviewapp.ui.activity;


import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.R;
import com.example.webviewapp.data.DataManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO:未解决DataManager单例初始化问题
        DataManager.init(this);
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
//        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_mainpage, null, false);
//        final PopupWindow popWindow = new PopupWindow(view,
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popWindow.setTouchable(true);
//        popWindow.setTouchInterceptor((v, event) -> {
//            Log.d("TAG", "onTouch: popwindowss");
//            return false;
//        });
//        popWindow.showAtLocation(view, 80, 0, 0);
    }

}

