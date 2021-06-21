package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.NewsAdapter;
import com.example.webviewapp.common.base.BaseFragment;
import com.example.webviewapp.data.NewsItem;
import com.example.webviewapp.databinding.FragmentNewsBinding;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends BaseFragment {
    private static final String TAG = "NewsFragment";
    public FragmentNewsBinding viewBinding;
    String jsonString;

    private List<NewsItem> news;

    public NewsFragment(String jsonString) {
        this.jsonString = jsonString;
        Log.d(TAG, "NewsFragment: " + jsonString);
        initNewsData(jsonString);
    }

    private void initNewsData(String jsonString) {
        news = new ArrayList<>();
        JSONObject json = JSON.parseObject(jsonString);
        JSONObject result = json.getJSONObject("result");
        JSONArray data = result.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject object = data.getJSONObject(i);
            NewsItem item = new NewsItem();

            item.setUniquekey(object.getString("uniquekey"));
            item.setTitle(object.getString("title"));
            item.setCategory(object.getString("category"));
            item.setAuthorName(object.getString("author_name"));
            item.setUrl(object.getString("url"));
            item.setIsContent(object.getString("is_content"));

            for (String key : object.keySet()) {
                if (key.contains("thumbnail_pic_s")) {
                    item.getThumbnailPics().add(object.getString(key));
                }
            }

            news.add(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding.newsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        viewBinding.newsList.setAdapter(new NewsAdapter(news, getActivity(), R.layout.news_item));
        return root;
    }
}
