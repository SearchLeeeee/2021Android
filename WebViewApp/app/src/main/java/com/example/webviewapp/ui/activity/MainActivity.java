package com.example.webviewapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.R;
import com.example.webviewapp.data.DataManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //TODO:未解决DataManager单例初始化问题
        DataManager.init(this);
        mainpage();
        startActivity(new Intent(MainActivity.this, RecordActivity.class));
    }


    public void mainpage() {
        // 组件注册
        WebView myWebView = findViewById(R.id.webview);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ListView listView = findViewById(R.id.listitem);
        ImageButton refreshButton = findViewById(R.id.refreshButton);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton forwardButton = findViewById(R.id.fowardButton);
        SearchView searchView = findViewById(R.id.searchbar);
        myWebView.loadUrl("http://www.baidu.com");

        myWebView.setWebViewClient(new WebViewClient() {
            //在webview里打开新链接
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        /**
         * 初始化菜单栏
         */
        initButton(myWebView, menuButton, refreshButton, backButton, forwardButton);
        initSearchbar(searchView, listView, myWebView);


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
    private void initButton(WebView myWebView, ImageButton menuButton, ImageButton refreshButton, ImageButton backButton, ImageButton forwardButton) {
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

