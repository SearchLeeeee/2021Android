package com.example.webviewapp.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.CustomWebViewClient;
import com.example.webviewapp.common.adapters.JavaScripInterfaceAdapter;
import com.example.webviewapp.data.DataManager;

//需求： 提取文字、代码重构、按键时事件的绑定,监听滚动事件

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String DEFAULT_URL = "file:///android_asset/index.html";

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //TODO:未解决DataManager单例初始化问题
        DataManager.init(this);
        mainpage();
        startActivity(new Intent(getApplication(), UserActivity.class));

    }

    @Override
    protected void onDestroy() {
        new Thread(() -> {
            // 子线程清空磁盘缓存
            Glide.get(MainActivity.this).clearDiskCache();
        }).start();
        // 主线程清空内存缓存
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    //处理返回键的监听事件

    public void mainpage() {
        // 组件注册
        myWebView = findViewById(R.id.webview);
        ImageView menuButton = findViewById(R.id.menuButton);
        ListView listView = findViewById(R.id.listitem);
        ImageView refreshButton = findViewById(R.id.refreshButton);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView forwardButton = findViewById(R.id.fowardButton);
        SearchView searchView = findViewById(R.id.searchbar);
        myWebView.loadUrl("https://www.baidu.com/");


        initWebView(myWebView);

        /**
         * 初始化菜单栏
         */
        initButton(myWebView, menuButton, refreshButton, backButton, forwardButton);
        initSearchbar(searchView, listView, myWebView);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView(WebView myWebView) {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.loadUrl(DEFAULT_URL);
        JavaScripInterfaceAdapter javaScripInterface = new JavaScripInterfaceAdapter(this);
        myWebView.addJavascriptInterface(javaScripInterface, "imagelistener");
        myWebView.setWebViewClient(new CustomWebViewClient());

    }

    private void initSearchbar(SearchView searchView, ListView listView, WebView myWebView) {
        searchView.setIconifiedByDefault(false);
        String[] note = {"人活着是为了什么1", "人活着是为了什么2", "人活着是为了什么3", "example1", "res"};
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, note));
        listView.setTextFilterEnabled(true);
        listView.setVisibility(View.GONE);


        listView.setOnItemClickListener((parent, view, position, id) -> {
            String result = parent.getItemAtPosition(position).toString();//获取选择项的值
            Toast.makeText(MainActivity.this, "您点击了" + result, Toast.LENGTH_SHORT).show();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = "https://wap.baidu.com/s?word=" + query;
                Log.d("TAG", "onQueryTextSubmit: " + url);
                myWebView.loadUrl(url);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listView.setVisibility(View.GONE);
                    listView.clearTextFilter();
                } else {
                    listView.setVisibility(View.VISIBLE);
                    listView.setFilterText(newText);
                }
                return true;
            }
        });



    }

    /**
     * @param myWebView
     * @param menuButton
     * @param refreshButton
     * @param backButton
     * @param forwardButton 实现了按钮和webview的初始化
     */
    private void initButton(WebView myWebView, ImageView menuButton, ImageView refreshButton, ImageView backButton, ImageView forwardButton) {
        //设置按钮的点击事件
        menuButton.setOnClickListener(v -> {
            Log.d("TAG", "菜单栏点击");
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
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


    /**
     * 菜单栏的实现
     */
    public void popwindow() {
        // PopWindow 布局发
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_mainpage, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor((v, event) -> {
                    Log.d("TAG", "onTouch: popwindowss");
                    return false;
                }
        );
        popWindow.showAtLocation(view, 80, 0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("TAG", "onBackPressed  22222 : 按下了返回键");
            myWebView.goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}

