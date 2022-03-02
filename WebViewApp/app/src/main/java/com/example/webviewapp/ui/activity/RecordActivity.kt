package com.example.webviewapp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.webviewapp.common.adapters.RecordViewPagerAdapter
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.databinding.ActivityRecordBinding
import com.example.webviewapp.ui.fragment.HistoryFragment
import com.example.webviewapp.ui.fragment.LabelFragment
import java.util.*

class RecordActivity : BaseActivity() {
    private val mFragmentList: MutableList<Fragment> = ArrayList<Fragment>()
    private val mTitleList: MutableList<String> = ArrayList()
    var viewBinding: ActivityRecordBinding? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar().hide()
        initViewPager()
        viewBinding!!.backBtn.setOnClickListener { v: View? -> finish() }
    }

    private fun initViewPager() {
        mFragmentList.add(LabelFragment())
        mTitleList.add("书签")
        mFragmentList.add(HistoryFragment())
        mTitleList.add("历史")
        val adapter = RecordViewPagerAdapter(getSupportFragmentManager(), mFragmentList, mTitleList)
        viewBinding!!.viewPager.setAdapter(adapter)
        viewBinding!!.tabs.setupWithViewPager(viewBinding!!.viewPager)
    }

    companion object {
        private const val TAG = "RecordActivity"
    }
}