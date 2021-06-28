package com.example.webviewapp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.CustomWebViewClient;
import com.example.webviewapp.common.adapters.JavaScripInterfaceAdapter;
import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.common.utils.AdBlocker;
import com.example.webviewapp.contract.MainContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.databinding.ActivityMainBinding;
import com.example.webviewapp.presenter.MainPresenter;

import java.util.List;

//TODO:删除无意义注释、无意义log
//TODO：Java驼峰命名，xml下划线和小写
//TODO：有复杂逻辑的地方要写注释
public class MainActivity extends BaseActivity implements MainContract.View {
    private static final String TAG = "MainActivity";
    private MainContract.Presenter presenter;
    public ActivityMainBinding viewBinding;

    public static final String DEFAULT_URL = "file:///android_asset/index.html";
    CustomWebViewClient webViewClient = new CustomWebViewClient(presenter) {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            viewBinding.progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            viewBinding.progressbar.setVisibility(View.VISIBLE);
        }
    };
    /**
     * WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private final WebChromeClient webChromeClient = new WebChromeClient() {
        //监听js alert弹窗事件
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            Log.i("get到的参数", message);
            if (message.equals("1")) {
                Log.i("弹窗", "继续访问:" + webViewClient.blockUrl);
                webView.loadUrl(webViewClient.blockUrl);
            } else {
                Log.i("弹窗", "停止访问");
                webView.goBack();
                webView.goBack();
            }

            result.confirm();
            return true;
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            viewBinding.progressbar.setProgress(newProgress);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        initUtils();
        initWebView();
        initButton();
        initSearchBar();

        //  startActivity(new Intent(getApplication(), UserActivity.class));
    }

    private void initSearchBar() {
        viewBinding.searchbar.setIconifiedByDefault(false);
        List<String> history = presenter.getHistory();
        viewBinding.listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, history));
        viewBinding.listView.setTextFilterEnabled(true);
        viewBinding.listView.setVisibility(View.GONE);

        viewBinding.listView.setOnItemClickListener((parent, view, position, id) -> {
            String result = parent.getItemAtPosition(position).toString();//获取选择项的值
            Toast.makeText(MainActivity.this, "您点击了" + result, Toast.LENGTH_SHORT).show();
        });

        viewBinding.searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = "https://wap.baidu.com/s?word=" + query;
                viewBinding.webview.loadUrl(url);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    viewBinding.listView.setVisibility(View.GONE);
                    viewBinding.listView.clearTextFilter();
                } else {
                    viewBinding.listView.setVisibility(View.VISIBLE);
                    viewBinding.listView.setFilterText(newText);
                }
                return true;
            }
        });
    }

    private void initButton() {
        //设置按钮的点击事件
        viewBinding.menuButton.setOnClickListener(v -> {
            Log.d("TAG", "菜单栏点击");
//            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
//            startActivity(intent);
            popWindow();
        });

        viewBinding.refreshButton.setOnClickListener(v -> {
            Log.d("TAG", "onClick:refresh");
            viewBinding.webview.clearCache(true);
            viewBinding.webview.reload();
        });

        viewBinding.backButton.setOnClickListener(v -> {
            if (viewBinding.webview.canGoBack()) viewBinding.webview.goBack();
            else viewBinding.webview.goBack();
            if (viewBinding.webview.getUrl().equals("file:///android_asset/askToJump.html")) {//在风险访问h5页面需要两次goback才能回去
                Log.i("TAG", "same");
                viewBinding.webview.goBack();
                viewBinding.webview.goBack();
            }
            Log.d("TAG", "mainpage: backward ");
        });
        viewBinding.fowardButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InfoReadActivity.class));
        });
    }

    private void initWebView() {
        viewBinding.webview.loadUrl("https://www.baidu.com/");
        WebSettings webSettings = viewBinding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        viewBinding.webview.loadUrl(DEFAULT_URL);
        JavaScripInterfaceAdapter javaScripInterface = new JavaScripInterfaceAdapter(this);
        viewBinding.webview.addJavascriptInterface(javaScripInterface, "imagelistener");
        viewBinding.webview.addJavascriptInterface(javaScripInterface, "blockListener");
        viewBinding.webview.setWebViewClient(webViewClient);
        viewBinding.webview.setWebChromeClient(webChromeClient);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initUtils() {
        //TODO:未解决DataManager单例初始化问题
        DataManager.init(this);
        AdBlocker.init(this);
        presenter = new MainPresenter(this);
    }

    @Override
    protected void onDestroy() {
        // 子线程清空磁盘缓存
        new Thread(() -> {
            Glide.get(MainActivity.this).clearDiskCache();
        }).start();
        // 主线程清空内存缓存
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    /**
     * 菜单栏的实现
     */
    public void popWindow() {
        // PopWindow 布局发
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_mainpage, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor((v, event) -> {
            return false;
        });
        popWindow.showAtLocation(view,Gravity.BOTTOM|Gravity.LEFT,0,-40);
        ImageView userButton = view.findViewById(R.id.user_image);
        ImageView historyButton = view.findViewById(R.id.history_image);
        ImageView addLabel = view.findViewById(R.id.addbookmark_image);
        userButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UserActivity.class));
        });
        historyButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RecordActivity.class));
        });
        addLabel.setOnClickListener(v -> addLabel(viewBinding.webview));
    }



    public void addLabel(WebView myWebView) {
        presenter.addLabel(myWebView.getTitle(), myWebView.getUrl());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (viewBinding.webview.getUrl().equals("file:///android_asset/askToJump.html") && keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("TAG", "same");
            viewBinding.webview.goBack();
            viewBinding.webview.goBack();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("TAG", "onBackPressed  22222 : 按下了返回键");
            viewBinding.webview.goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}

