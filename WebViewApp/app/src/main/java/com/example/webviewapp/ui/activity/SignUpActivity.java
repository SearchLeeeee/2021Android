package com.example.webviewapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.R;
import com.example.webviewapp.common.utils.Cloud.CloudUser;
import com.example.webviewapp.contract.SignupContract;
import com.example.webviewapp.data.User;
import com.example.webviewapp.databinding.FragmentRegisterBinding;
import com.example.webviewapp.presenter.SignUpPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements SignupContract.View {
    private static final String TAG = "SignUpActivity";
    public FragmentRegisterBinding viewBinding;
    boolean flag = false;
    private SignupContract.Presenter presenter;
    private int clickedImageID = 2131230895;
    private String Email;
    private String Password;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SignUpPresenter();
        viewBinding = FragmentRegisterBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        initButton();
    }

    /**
     * 初始化界面
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initButton() {
        viewBinding.signupButton.setOnClickListener(v -> signUp());
        viewBinding.cancelButton.setOnClickListener(v -> back());
        viewBinding.includeAvatarPicker.imageView.setOnClickListener(v ->click(viewBinding.includeAvatarPicker.imageView));
        viewBinding.includeAvatarPicker.imageView2.setOnClickListener(v->click(viewBinding.includeAvatarPicker.imageView2));
        viewBinding.includeAvatarPicker.imageView3.setOnClickListener(v->click(viewBinding.includeAvatarPicker.imageView3));
        viewBinding.includeAvatarPicker.imageView4.setOnClickListener(v->click(viewBinding.includeAvatarPicker.imageView4));
        viewBinding.includeAvatarPicker.imageView5.setOnClickListener(v->click(viewBinding.includeAvatarPicker.imageView5));
        viewBinding.includeAvatarPicker.imageView6.setOnClickListener(v->click(viewBinding.includeAvatarPicker.imageView6));
    }

    /**
     * 注册功能的实现
     * 将注册数据写进数据库中
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signUp() {
//        if (!presenter.ContainsUid(viewBinding.UserNumber.getText())) {
//            presenter.SignUp(viewBinding.UserNumber.getText(), viewBinding.loginPassword.getText());
//            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//            this.finish();
//        } else {
//            Toast.makeText(getApplicationContext(), "用户已存在，注册失败", Toast.LENGTH_SHORT).show();
//        }
        Log.i(TAG, "图片id: "+ clickedImageID);

        Email = viewBinding.UserNumber.getText().toString().trim();
        Password = viewBinding.loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(Email)){
            Toast.makeText(getApplicationContext(), "请输入您的Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (!checkEmail(Email)){
            Toast.makeText(getApplicationContext(), "请输入正确邮箱格式", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)){
            Toast.makeText(getApplicationContext(), "请输入您的密码", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length()<6){
            Toast.makeText(getApplicationContext(), "密码不能少于六位", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("正在为您创建用户...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerification();
                    mDialog.dismiss();
                    OnAuth(task.getResult().getUser());
                    mAuth.signOut();
                    presenter.SignUp();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "创建用户失败，可能原因:\n1.网络连接失败\n2.该账号已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 验证邮箱的正则表达式
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        String rule = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(rule);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "请在邮箱中验证", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid) {
        User user = BuildNewUser();
        CloudUser.get().uploadUser(uid, user);
    }

    private User BuildNewUser() {
        return new User(
                getUserEmail(),
                clickedImageID
        );
    }

    public String getUserEmail() {
        return Email;
    }

    public void back() {
        this.finish();
    }

    @SuppressLint("NonConstantResourceId")
    public void click(View v) {
        if (flag) {
            switch (clickedImageID) {
                case R.id.imageView:
                    viewBinding.includeAvatarPicker.imageView.setImageResource(R.drawable.circle);
                    break;
                case R.id.imageView2:
                    viewBinding.includeAvatarPicker.imageView2.setImageResource(R.drawable.circle);
                    break;
                case R.id.imageView3:
                    viewBinding.includeAvatarPicker.imageView3.setImageResource(R.drawable.circle);
                    break;
                case R.id.imageView4:
                    viewBinding.includeAvatarPicker.imageView4.setImageResource(R.drawable.circle);
                    break;
                case R.id.imageView5:
                    viewBinding.includeAvatarPicker.imageView5.setImageResource(R.drawable.circle);
                    break;
                case R.id.imageView6:
                    viewBinding.includeAvatarPicker.imageView6.setImageResource(R.drawable.circle);
                    break;
                default:
                    break;
            }
        }
        switch (v.getId()) {
            case R.id.imageView:
                viewBinding.includeAvatarPicker.imageView.setImageResource(R.drawable.purple_frame);
                break;
            case R.id.imageView2:
                viewBinding.includeAvatarPicker.imageView2.setImageResource(R.drawable.purple_frame);
                break;
            case R.id.imageView3:
                viewBinding.includeAvatarPicker.imageView3.setImageResource(R.drawable.purple_frame);
                break;
            case R.id.imageView4:
                viewBinding.includeAvatarPicker.imageView4.setImageResource(R.drawable.purple_frame);
                break;
            case R.id.imageView5:
                viewBinding.includeAvatarPicker.imageView5.setImageResource(R.drawable.purple_frame);
                break;
            case R.id.imageView6:
                viewBinding.includeAvatarPicker.imageView6.setImageResource(R.drawable.purple_frame);
                break;
            default:
                break;
        }
        clickedImageID = v.getId();
        flag = true;
    }
}