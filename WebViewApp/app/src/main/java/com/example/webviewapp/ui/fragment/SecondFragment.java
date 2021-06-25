package com.example.webviewapp.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.webviewapp.databinding.FragmentSecondBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;


//import com.example.webviewapp.ui.activity.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fb
        //获取用户信息
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        String email;
        if (user != null) {
            uid = user.getUid();
            email = user.getEmail();
            Log.i("TAG", "id: "+ uid);
        } else {
            uid = "";
            email = "";
        }
        binding.textviewSecond.setText(email);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}