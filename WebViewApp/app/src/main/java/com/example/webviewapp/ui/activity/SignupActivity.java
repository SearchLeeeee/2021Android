package com.example.webviewapp.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.contract.SignupContract;
import com.example.webviewapp.databinding.SignupactivityBinding;
import com.example.webviewapp.presenter.SignUpPresenter;

public class SignupActivity extends AppCompatActivity implements SignupContract.View {

    private static final String TAG = "SignupActivity";
    SignupactivityBinding viewBinding;
    SignupContract.Presenter presenter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SignUpPresenter();
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
        if (!presenter.ContainsUid(viewBinding.UserNumber.getText())) {
            presenter.SignUp(viewBinding.UserNumber.getText(), viewBinding.loginPassword.getText());
            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            Toast.makeText(getApplicationContext(), "用户已存在，注册失败", Toast.LENGTH_SHORT).show();
        }

    }

    public void back() {
        this.finish();
    }
}