package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.webviewapp.common.utils.Cloud.CloudUser;
import com.example.webviewapp.databinding.FragmentSecondBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    String email;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserShow();
//        binding.textviewSecond.setText(email);
    }


    private void getUserShow(){
        //fb
        //获取用户信息
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) {
            uid = user.getUid();
//            email = user.getEmail();
            CloudUser cloudUser = new CloudUser(getContext());
            email = cloudUser.getUserCloud(uid).getEmail();
            binding.textviewSecond.setText(email);
            Log.i("TAG", "id: "+ uid);
            Log.i("TAG", "onViewCreated: " + email);
        } else {
            uid = "";
            email = "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}