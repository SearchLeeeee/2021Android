package com.example.webviewapp.ui.fragment.signUp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.webviewapp.R;
import com.example.webviewapp.common.utils.Cloud.CloudUser;
import com.example.webviewapp.common.utils.EventUtils;
import com.example.webviewapp.contract.LoginContract;
import com.example.webviewapp.databinding.FragmentSecondBinding;
import com.example.webviewapp.presenter.LoginPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondFragment extends Fragment {
    private static final String TAG = "SecondFragment";
    LoginContract.Presenter presenter = new LoginPresenter();

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
        Button logoutBtn = binding.logout;
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            presenter.logout();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });
    }

    private void getUserShow(){
        //获取用户信息
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) {
            uid = user.getUid();
            CloudUser.get().getUserCloud(uid);
            Log.i(TAG, "id: " + uid);
        } else {
            email = "";
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmailEvent(EventUtils.UserEvent event) {
        binding.textviewSecond.setText(event.email);
        selectImg(event.avatarId);
    }

    public void selectImg(int avatarId){
        switch (avatarId){
            case 2131230895:
                binding.imageView5.setImageResource(R.drawable.avatar_1_raster);
                break;
            case 2131230896:
                binding.imageView5.setImageResource(R.drawable.avatar_2_raster);
                break;
            case 2131230897:
                binding.imageView5.setImageResource(R.drawable.avatar_3_raster);
                break;
            case 2131230898:
                binding.imageView5.setImageResource(R.drawable.avatar_4_raster);
                break;
            case 2131230899:
                binding.imageView5.setImageResource(R.drawable.avatar_5_raster);
                break;
            case 2131230900:
                binding.imageView5.setImageResource(R.drawable.avatar_6_raster);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        EventBus.getDefault().unregister(this);
    }
}