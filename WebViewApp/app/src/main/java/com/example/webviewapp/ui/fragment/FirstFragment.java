package com.example.webviewapp.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.webviewapp.R;
import com.example.webviewapp.contract.LoginContract;
import com.example.webviewapp.databinding.FragmentFirstBinding;
import com.example.webviewapp.presenter.LoginPresenter;
import com.example.webviewapp.ui.activity.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FirstFragment extends Fragment implements LoginContract.View {

    LoginContract.Presenter presenter = new LoginPresenter();
    private FragmentFirstBinding binding;

    //fb
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * 初始化登录窗口
     * 还有登录逻辑
     * 数据可以与数据库交互
     */
    private AlertDialog dialog;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> loginWindow());
        binding.signupButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SignUpActivity.class)));
    }

    private void loginWindow() {
        // 登录窗口的控件绑定
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        dialog = builder.create();
        View dialogView = View.inflate(getContext(), R.layout.loginwindow, null);
        Button loginButton = dialogView.findViewById(R.id.loginButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        EditText uidtext = dialogView.findViewById(R.id.UserNumber);
        EditText passwordtext = dialogView.findViewById(R.id.loginPassword);

        dialog.setView(dialogView);
        dialog.show();

        //fb
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    dialog.dismiss();
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    onDestroyView();
                } else {
                    Log.d("TAG", "AuthStateChanged:Logout");
                }

            }
        };

        // 登录事件绑定
        loginButton.setOnClickListener(v -> {
//            if (presenter.Login(uidtext.getText(), passwordtext.getText())) {
//                dialog.dismiss();
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                onDestroyView();
//            } else {
//                Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
//            }

            String email, password;
            email = uidtext.getText().toString().trim();
            password = passwordtext.getText().toString().trim();
            userSign(email, password);
            boolean isLogin;
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //fb
    private void userSign(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter the correct Email", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Enter the correct password", Toast.LENGTH_SHORT).show();
            return;
        }

        //fb
        ProgressDialog mDialog;
        mDialog = new ProgressDialog(getContext());

        mDialog.setMessage("Loging in please wait...");
        mDialog.setIndeterminate(true);
        mDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(getContext(), "Login not successfully", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    checkIfEmailVerified();
                }
            }
        });
    }

    /**
     * 检查将要注册的email是否已经注册了
     */
    private void checkIfEmailVerified() {
        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified = users.isEmailVerified();
        if (!emailVerified) {
            Toast.makeText(getContext(), "Verify the Email Id", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
//            finish();
        } else {
            dialog.dismiss();
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
            onDestroyView();
        }
    }
}