package com.example.webviewapp.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.User;
import com.example.webviewapp.databinding.SignupactivityBinding;

public class SignupActivity extends AppCompatActivity {

    SignupactivityBinding viewBinding;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = SignupactivityBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        init();
    }

    /**
     * 初始化界面
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init() {
        viewBinding.signupButton.setOnClickListener(v -> signup());
        viewBinding.cancelButton.setOnClickListener(v -> back());
    }

    /**
     * 注册功能的实现
     * 将注册数据写进数据库中
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signup() {
        long uid = Long.parseLong(viewBinding.UserNumber.getText().toString());
        long password = Long.parseLong(viewBinding.loginPassword.getText().toString());
        User user = new User(uid, password);
        DataManager dataManager = new DataManager(getApplicationContext());
        dataManager.addUser(user);
        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void back() {
        this.finish();
    }
}