package com.example.webviewapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.common.utils.DataFormatUtils
import com.example.webviewapp.common.utils.EventUtils
import com.example.webviewapp.common.utils.EventUtils.NewsDataChangeEvent
import com.example.webviewapp.databinding.ActivityInfoReadBinding
import com.example.webviewapp.ui.activity.InfoReadActivity
import com.example.webviewapp.ui.fragment.NewsFragment
import okhttp3.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*

class InfoReadActivity : BaseActivity() {
    private val fragments: MutableList<Fragment> = ArrayList()
    private val titles: MutableList<String> = ArrayList()
    var viewBinding: ActivityInfoReadBinding? = null
    private val pagerAdapter: FragmentPagerAdapter = object : FragmentPagerAdapter(this.supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        EventUtils.register(this)
        initButton()
        initData()
        initViewPager()
    }

    private fun initButton() {
        viewBinding!!.backBtn.setOnClickListener { v: View? -> finish() }
    }

    private fun initData() {
        for (i in type_en.indices) {
            titles.add(type_cn[i])
            //            query(type_en[i]);
            queryLocal(type_en[i])
        }
    }

    private fun queryLocal(type: String) {
        fragments.add(NewsFragment(DataFormatUtils.getJson(this@InfoReadActivity, "$type.json")))
    }

    private fun initViewPager() {
        viewBinding!!.viewpager.adapter = pagerAdapter
        viewBinding!!.tabs.setupWithViewPager(viewBinding!!.viewpager)
    }

    private fun query(type: String) {
        val client = OkHttpClient()
        Log.d(TAG, "query: $URL_HOST$type")
        val request = Request.Builder()
                .url(URL_HOST + type)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@InfoReadActivity, "获取不到信息，请检查您的网络情况！", Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body != null) {
                    fragments.add(NewsFragment(response.body!!.string()))
                    EventUtils.post(NewsDataChangeEvent())
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewsDataChangeEvent(event: NewsDataChangeEvent?) {
        pagerAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        EventUtils.unregister(this)
        super.onDestroy()
    }

    companion object {
        const val URL_HOST = "http://v.juhe.cn/toutiao/index?key=8cc3761c4e5d283b49e8d5062ebc2ab6&type="
        val type_en = arrayOf("top", "guonei", "guoji", "yule", "tiyu", "junshi", "keji", "caijing", "shishang", "youxi", "qiche", "jiankang")
        val type_cn = arrayOf("推荐", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚", "游戏", "汽车", "健康")
        private const val TAG = "InfoReadActivity"
    }
}