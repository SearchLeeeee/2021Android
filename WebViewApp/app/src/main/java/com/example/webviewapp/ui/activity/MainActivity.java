package com.example.webviewapp.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.R;
import com.example.webviewapp.data.DataManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO:未解决DataManager单例初始化问题
        DataManager.init(this);
    }

}