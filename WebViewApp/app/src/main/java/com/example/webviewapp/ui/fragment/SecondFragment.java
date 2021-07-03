package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.webviewapp.common.utils.Cloud.CloudUser;
import com.example.webviewapp.data.EventManager;
import com.example.webviewapp.databinding.FragmentSecondBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondFragment extends Fragment {
    private static final String TAG = "SecondFragment";

    private FragmentSecondBinding binding;
    String email;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        EventBus.getDefault().register(this);
        getUserShow();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void getUserShow(){
        //fb
        //获取用户信息
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) {
            uid = user.getUid();
//            email = user.getEmail();
            CloudUser.get().getUserCloud(uid);
            Log.i(TAG, "id: " + uid);
        } else {
            uid = "";
            email = "";
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmailEvent(EventManager.EmailEvent event) {
        binding.textviewSecond.setText(event.email);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        EventBus.getDefault().unregister(this);
    }
}