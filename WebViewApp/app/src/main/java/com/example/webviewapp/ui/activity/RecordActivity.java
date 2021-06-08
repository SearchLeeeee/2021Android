package com.example.webviewapp.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.webviewapp.common.adapters.RecordViewPagerAdapter;
import com.example.webviewapp.databinding.ActivityRecordBinding;
import com.example.webviewapp.ui.fragment.HistoryFragment;
import com.example.webviewapp.ui.fragment.LabelFragment;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private static final String TAG = "RecordActivity";
    ActivityRecordBinding viewBinding;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        getSupportActionBar().hide();

        initViewPager();
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