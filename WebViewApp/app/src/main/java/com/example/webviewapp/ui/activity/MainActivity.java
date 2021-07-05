package com.example.webviewapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.CustomWebViewClient;
import com.example.webviewapp.common.adapters.JavaScripInterfaceAdapter;
import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.contract.MainContract;
import com.example.webviewapp.databinding.ActivityMainBinding;
import com.example.webviewapp.presenter.MainPresenter;

import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View {
    private static final String TAG = "MainActivity";
    private MainContract.Presenter presenter;
    public ActivityMainBinding viewBinding;
    private PopupWindow popWindow;
    private View view;
    private TextView addedText;
    private ImageView addLabel;
    public static final String DEFAULT_URL = "file:///android_asset/index.html";
    CustomWebViewClient webViewClient = new CustomWebViewClient(presenter) {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            presenter.addHistory(url, view.getTitle());
            viewBinding.progressbar.setVisibility(View.GONE);
            initUrl(view);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            viewBinding.progressbar.setVisibility(View.VISIBLE);
            if (presenter.getLabelUrl().contains(viewBinding.webview.getUrl())) {
                addedText.setText("已添加");
                addLabel.setImageResource(R.drawable.collected);
            } else {
                addedText.setText("添加");
                addLabel.setImageResource(R.drawable.uncollected);
            }
        }
    };

    private void initUrl(WebView view) {
        view.loadUrl("javascript:(function()" +
                "    {" +
                "        var objs = document.getElementsByTagName('img');" +
                "        var array=new Array();" +
                "        for(var j=0;j<objs.length;j++)" +
                "            {" +
                "                array[j]=objs[j].src;" +
                "            }" +
                "        for(var i=0;i<objs.length;i++)" +
                "            {" +
                "                objs[i].onclick=function()" +
                "                    {" +
                "                        window.imagelistener.openImage(this.src,array);" +
                "                    }" +
                "            }" +
                "    })()");
        view.loadUrl("javascript:(function()" +
                "    {" +
                "        var objs = document.getElementsByTagName('video');" +
                "        for(var i=0;i<objs.length;i++)" +
                "            {" +
                "                objs[i].addEventListener('play', function () {" +
                "                   window.imagelistener.openVideo(this.currentSrc);" +
                "                   this.pause();"+
                "                });" +
                "            }" +
                "    })()");
    }
    /**
     * WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private final WebChromeClient webChromeClient = new WebChromeClient() {
        //监听js alert弹窗事件
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            if (message.equals("1")) {
                Log.i("弹窗", "继续访问:" + webViewClient.blockUrl);
                webView.loadUrl(webViewClient.blockUrl);
            } else if (message.equals("0")){
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
        presenter = new MainPresenter(this);

        initWebView();
        initButton();
        initSearchBar();

    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getStringExtra("url") != null) {
            viewBinding.webview.loadUrl(intent.getStringExtra("url"));
        }
    }

    private void initSearchBar() {
        viewBinding.searchbar.setIconifiedByDefault(false);
        List<String> history = presenter.getHistory();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, history);
        viewBinding.listView.setAdapter(adapter);
        viewBinding.listView.setTextFilterEnabled(true);
        viewBinding.listView.setVisibility(View.GONE);

        viewBinding.listView.setOnItemClickListener((parent, view, position, id) -> {
            String result = parent.getItemAtPosition(position).toString();//获取选择项的值
            Toast.makeText(MainActivity.this, "您点击了" + result, Toast.LENGTH_SHORT).show();
        });

        viewBinding.searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    private void initButton() {
        //设置按钮的点击事件
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_mainpage, null, false);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        initPopWindow();
        viewBinding.menuButton.setOnClickListener(v -> {
            int height = dm.heightPixels;
            Log.d(TAG, "initButton: ");
            if (popWindow.isShowing()) popWindow.dismiss();
            else {
                popWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, dm.heightPixels - 400);
            }
        });

        viewBinding.refreshButton.setOnClickListener(v -> {
            viewBinding.webview.loadUrl(DEFAULT_URL);
        });


        viewBinding.backButton.setOnClickListener(v -> {
            if (viewBinding.webview.canGoBack())
                viewBinding.webview.goBack();
            else onBackPressed();
            if (viewBinding.webview.getUrl().equals("file:///android_asset/askToJump.html")) {//在风险访问h5页面需要两次goback才能回去
                Log.i("TAG", "same");
                viewBinding.webview.goBack();
                viewBinding.webview.goBack();
            }
        });
        viewBinding.nextButton.setOnClickListener(v -> {
            viewBinding.webview.goForward();
        });
        viewBinding.fowardButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InfoReadActivity.class));
        });
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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
    @SuppressLint("ClickableViewAccessibility")
    public void initPopWindow() {
        // PopWindow 布局发
        popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setAnimationStyle(R.anim.nav_default_pop_enter_anim);
        popWindow.setTouchInterceptor((v, event) -> {
            return false;
        });

        LinearLayout userCenter = view.findViewById(R.id.user_center);
        RelativeLayout quitButton = view.findViewById(R.id.quit_button);
        RelativeLayout historyButton = view.findViewById(R.id.history_button);
        RelativeLayout refreshButton = view.findViewById(R.id.refresh_button);
        RelativeLayout addLabelButton = view.findViewById(R.id.add_bookmark_button);
        addLabel = view.findViewById(R.id.add_bookmark_image);
        addedText = view.findViewById(R.id.add_bookmark_text);

        userCenter.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UserActivity.class));
        });
        historyButton.setOnClickListener(v -> {
            popWindow.dismiss();
            startActivity(new Intent(MainActivity.this, RecordActivity.class));
        });
        addLabelButton.setOnClickListener(v -> {
            if (addedText.getText().toString().equals("添加")) {
                addLabel();
                addedText.setText("已添加");
                addLabel.setImageResource(R.drawable.collected);
            } else {
                deleteLabel();
                addedText.setText("添加");
                addLabel.setImageResource(R.drawable.uncollected);
            }
        });
        quitButton.setOnClickListener(v -> finish());
        refreshButton.setOnClickListener(v -> {
            viewBinding.webview.reload();
            Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
        });
    }

    public void addLabel() {
        Toast.makeText(this, "书签添加成功", Toast.LENGTH_SHORT).show();
        presenter.addLabel(viewBinding.webview.getUrl(), viewBinding.webview.getTitle());

    }

    public void deleteLabel() {
        Toast.makeText(this, "取消书签收藏", Toast.LENGTH_SHORT).show();
        presenter.deleteRecord(viewBinding.webview.getUrl());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!viewBinding.webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        if (viewBinding.webview.getUrl().equals("file:///android_asset/askToJump.html") && keyCode == KeyEvent.KEYCODE_BACK) {
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

