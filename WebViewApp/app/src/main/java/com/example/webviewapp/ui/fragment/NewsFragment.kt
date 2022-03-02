package com.example.webviewapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.example.webviewapp.R
import com.example.webviewapp.common.adapters.NewsAdapter
import com.example.webviewapp.data.NewsItem
import com.example.webviewapp.databinding.FragmentNewsBinding
import java.util.*

class NewsFragment(private val jsonString: String) : Fragment() {
    var viewBinding: FragmentNewsBinding? = null
    private var news: MutableList<NewsItem>? = null
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = FragmentNewsBinding.inflate(inflater, container, false)
        viewBinding!!.newsList.setLayoutManager(LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false))
        viewBinding!!.newsList.setAdapter(NewsAdapter(news, getActivity(), R.layout.news_item))
        return viewBinding!!.root
    }

    private fun initNewsData(jsonString: String) {
        news = ArrayList()
        val json = JSON.parseObject(jsonString)
        Log.d(TAG, "initNewsData: $json")
        val result = json.getJSONObject("result")
        val data = result.getJSONArray("data")
        for (i in data.indices) {
            val `object` = data.getJSONObject(i)
            val item = NewsItem()
            initItem(`object`, item)
            for (key in `object`.keys) {
                if (key.contains("thumbnail_pic_s")) {
                    item.thumbnailPics.add(`object`.getString(key))
                }
            }
            news.add(item)
        }
    }

    private fun initItem(`object`: JSONObject, item: NewsItem) {
        item.uniquekey = `object`.getString("uniquekey")
        item.title = `object`.getString("title")
        item.category = `object`.getString("category")
        item.authorName = `object`.getString("author_name")
        item.url = `object`.getString("url")
        item.isContent = `object`.getString("is_content")
    }

    companion object {
        private const val TAG = "NewsFragment"
    }

    init {
        initNewsData(jsonString)
    }
}