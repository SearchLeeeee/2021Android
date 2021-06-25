package com.example.webviewapp.ui.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.contract.SignupContract;
import com.example.webviewapp.data.User;
import com.example.webviewapp.databinding.SignupactivityBinding;
import com.example.webviewapp.presenter.SignUpPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class SignupActivity extends AppCompatActivity implements SignupContract.View {

    private static final String TAG = "SignupActivity";
    public SignupactivityBinding viewBinding;
    SignupContract.Presenter presenter;
    //fb
    String Email,Password;
    ProgressDialog mDialog;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SignUpPresenter();
        viewBinding = SignupactivityBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        //fb
        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

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
//        if (!presenter.ContainsUid(viewBinding.UserNumber.getText())) {
//            presenter.SignUp(viewBinding.UserNumber.getText(), viewBinding.loginPassword.getText());
//            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//            this.finish();
//        } else {
//            Toast.makeText(getApplicationContext(), "用户已存在，注册失败", Toast.LENGTH_SHORT).show();
//        }

        //fb
        Email = viewBinding.UserNumber.getText().toString().trim();
        Password = viewBinding.loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(Email)){
            Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Password)){
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length()<6){
            Toast.makeText(getApplicationContext(),"Password must be greater then 6 digit",Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Creating User please wait...");
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
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message");

                    myRef.setValue("Hello, World!");

                }else{
                    Toast.makeText(getApplicationContext(),"error on creating user",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Email verification code using FirebaseUser object and using isSucccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Check your Email for verification",Toast.LENGTH_SHORT).show();
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
        User user = BuildNewuser();
        //无法添加到数据库
        mdatabase.child(uid).setValue(user);
    }


    private User BuildNewuser(){
        return new User(
                getUserEmail()
        );
    }

    public String getUserEmail() {
        return Email;
    }

    public void back() {
        this.finish();
    }
}