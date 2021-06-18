package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.webviewapp.databinding.ActivityInfoReadBinding;
import com.example.webviewapp.ui.fragment.NewsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoReadActivity extends AppCompatActivity {
    public static final String URL_HOST = "http://v.juhe.cn/toutiao/index?key=8cc3761c4e5d283b49e8d5062ebc2ab6&type=";
    public static final String[] type_en = {"top", "guonei", "guoji", "yule", "tiyu", "junshi", "keji", "caijing", "shishang", "youxi", "qiche", "jiankang"};
    public static final String[] type_cn = {"推荐", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚", "游戏", "汽车", "健康"};
    private static final String TAG = "InfoReadActivity";
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();
    ActivityInfoReadBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityInfoReadBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        getSupportActionBar().hide();

        initData();
        initViewPager();
    }

    private void initData() {
        //TODO:修改创建数量 type_en.length
        for (int i = 0; i < 1; i++) {
            query(type_en[i]);
            titles.add(type_cn[i]);
        }

        //TODO:本地测试数据
//        String input;
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getAssets().open("news.json")));
//            String line;
//            StringBuilder builder = new StringBuilder();
//            while ((line = bufferedReader.readLine()) != null) {
//                builder.append(line);
//            }
//            input = builder.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            input = "";
//        }
//        for (int i = 0; i < 12; i++) {
//            fragments.add(new NewsFragment(input));
//            titles.add("推荐");
//        }

    }

    private void initViewPager() {
        viewBinding.viewpager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        viewBinding.tabs.setupWithViewPager(viewBinding.viewpager);
    }

    private void query(String type) {
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "query: " + URL_HOST + type);
        Request request = new Request.Builder()
                .url(URL_HOST + type)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (request.body() != null) {
                    fragments.add(new NewsFragment(request.body().toString()));
                    Log.d(TAG, "onResponse: not null");
                }
                Log.d(TAG, "onResponse: ");
            }
        });
    }
}