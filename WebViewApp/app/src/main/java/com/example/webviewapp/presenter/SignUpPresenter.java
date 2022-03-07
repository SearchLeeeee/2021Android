package com.example.webviewapp.presenter;

import android.text.Editable;
import android.util.Log;

import com.example.webviewapp.contract.SignUpContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.User;

import java.util.List;

public class SignUpPresenter implements SignUpContract.Presenter {

@Override
public void signUp() {
    Log.d("TAG", "SignUp:  succssed");
}


    @Override
    public Boolean containsUid(Editable uidText) {
        if (uidText.toString().equals("")) return true;
        long uid = Long.parseLong(uidText.toString());
//        return DataManager.get().queryUserPasswordByUid(uid) != -1;
        return true;
    }

    @Override
    public List<User> getUser() {
        return DataManager.get().userList;
    }
}
