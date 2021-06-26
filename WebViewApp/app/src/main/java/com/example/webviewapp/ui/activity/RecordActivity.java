package com.example.webviewapp.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.webviewapp.common.adapters.RecordViewPagerAdapter;
import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.databinding.ActivityRecordBinding;
import com.example.webviewapp.ui.fragment.HistoryFragment;
import com.example.webviewapp.ui.fragment.LabelFragment;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends BaseActivity {
    private static final String TAG = "RecordActivity";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTitleList = new ArrayList<>();
    public ActivityRecordBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        initViewPager();

        viewBinding.backBtn.setOnClickListener(v -> finish());
    }

    private void initViewPager() {
        mFragmentList.add(new LabelFragment());
        mTitleList.add("书签");
        mFragmentList.add(new HistoryFragment());
        mTitleList.add("历史");
        RecordViewPagerAdapter adapter = new RecordViewPagerAdapter(getSupportFragmentManager(), mFragmentList, mTitleList);
        viewBinding.viewPager.setAdapter(adapter);
        viewBinding.tabs.setupWithViewPager(viewBinding.viewPager);
    }
}