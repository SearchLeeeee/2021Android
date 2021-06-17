package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.webviewapp.databinding.ActivityInfoReadBinding;
import com.example.webviewapp.ui.fragment.NewsFragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoReadActivity extends AppCompatActivity {
    public static final String URL_HOST = "https://v.juhe.cn/toutiao/index?key=8cc3761c4e5d283b49e8d5062ebc2ab6&?type=";
    public static final String[] type_en = {"top", "guonei", "guoji", "yule", "tiyu", "junshi", "keji", "caijing", "shishang", "youxi", "qiche", "jiankang"};
    public static final String[] type_cn = {"推荐", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚", "游戏", "汽车", "健康"};
    private static final String TAG = "InfoReadActivity";
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();
    ActivityInfoReadBinding viewBinding;
    Handler queryResultHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                fragments.add(new NewsFragment(msg.getData().getString("msg")));
            }
        }
    };

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
        //TODO:修改创建数量
        for (int i = 0; i < 2; i++) {
            query(type_en[i]);
            titles.add(type_cn[i]);
        }
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
        new Thread(() -> {
            Message message = Message.obtain();
            OkHttpClient client = new OkHttpClient();
            try {
                Log.d(TAG, "query: " + URL_HOST + type);
                Request request = new Request.Builder()
                        .url(URL_HOST + type)
                        .build();
                Call call = client.newCall(request);

                Response response = call.execute();
                Bundle bundle = new Bundle();
                bundle.putString("msg", response.body().string());
                message.setData(bundle);
                message.what = 1;
                //Log.d(TAG, "query: " + response.body().string());

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "query: query fail");
            }
            queryResultHandler.sendMessage(message);
        }).start();
    }
}