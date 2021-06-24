package com.example.webviewapp.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.example.webviewapp.ui.activity.SignupActivity;


@RequiresApi(api = Build.VERSION_CODES.N)
public class FirstFragment extends Fragment implements LoginContract.View {

    LoginContract.Presenter presenter = new LoginPresenter();
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonFirst.setOnClickListener(view1 -> loginwindows());
        binding.signupButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SignupActivity.class)));
    }

    /**
     * 初始化登录窗口
     * 还有登录逻辑
     * 数据可以与数据库交互
     */
    private void loginwindows() {

        /**
         * 登录窗口的控件绑定
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.create();
        View dialogview = View.inflate(getContext(), R.layout.loginwindow, null);
        Button loginButton = dialogview.findViewById(R.id.loginButton);
        Button cancelButton = dialogview.findViewById(R.id.cancelButton);
        EditText uidtext = dialogview.findViewById(R.id.UserNumber);
        EditText passwordtext = dialogview.findViewById(R.id.loginPassword);

        dialog.setView(dialogview);
        dialog.show();

        //
        /**
         * 登录事件绑定
         *
         */

        loginButton.setOnClickListener(v -> {
            if (presenter.Login(uidtext.getText(), passwordtext.getText())) {
                dialog.dismiss();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                onDestroyView();
            } else {
                Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}