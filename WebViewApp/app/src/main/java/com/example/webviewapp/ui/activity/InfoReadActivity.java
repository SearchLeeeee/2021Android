package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.common.utils.DataFormatUtils;
import com.example.webviewapp.common.utils.EventUtils;
import com.example.webviewapp.databinding.ActivityInfoReadBinding;
import com.example.webviewapp.ui.fragment.NewsFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoReadActivity extends BaseActivity {
    public static final String URL_HOST = "http://v.juhe.cn/toutiao/index?key=8cc3761c4e5d283b49e8d5062ebc2ab6&type=";
    public static final String[] type_en = {"top", "guonei", "guoji", "yule", "tiyu", "junshi", "keji", "caijing", "shishang", "youxi", "qiche", "jiankang"};
    public static final String[] type_cn = {"推荐", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚", "游戏", "汽车", "健康"};
    private static final String TAG = "InfoReadActivity";
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();
    public ActivityInfoReadBinding viewBinding;
    private final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(this.getSupportFragmentManager()) {
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

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EventUtils.register(this);

        initButton();
        initData();
        initViewPager();
    }

    private void initButton() {
        viewBinding.backBtn.setOnClickListener(v -> finish());
    }

    private void initData() {
        for (int i = 0; i < type_en.length; i++) {
            titles.add(type_cn[i]);
//            query(type_en[i]);
            queryLocal(type_en[i]);
        }
    }

    private void queryLocal(String type) {
        fragments.add(new NewsFragment(DataFormatUtils.getJson(InfoReadActivity.this, type + ".json")));
    }

    private void initViewPager() {
        viewBinding.viewpager.setAdapter(pagerAdapter);
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(InfoReadActivity.this, "获取不到信息，请检查您的网络情况！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    fragments.add(new NewsFragment(response.body().string()));
                    EventUtils.post(new EventUtils.NewsDataChangeEvent());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewsDataChangeEvent(EventUtils.NewsDataChangeEvent event) {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        EventUtils.unregister(this);
        super.onDestroy();
    }
}