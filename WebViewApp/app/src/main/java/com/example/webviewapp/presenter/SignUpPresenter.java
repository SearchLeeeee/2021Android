package com.example.webviewapp.presenter;

import android.text.Editable;
import android.util.Log;

import com.example.webviewapp.contract.SignupContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.User;

import java.util.List;

public class SignUpPresenter implements SignupContract.Presenter {
//TODO：改正确MVP
    @Override
    public void SignUp(Editable uidText, Editable passwordText) {
        long uid = Long.parseLong(uidText.toString());
        long password = Long.parseLong(passwordText.toString());
        User user = new User(uid, password);
        DataManager.get().addUser(user);
    }

    @Override
    public Boolean ContainsUid(Editable uidText) {

        if (uidText.toString().equals("")) return true;
        long uid = Long.parseLong(uidText.toString());
        Log.d("TAG", "ContainsUid: "+DataManager.get());
        return DataManager.get().queryUserPasswordByUid(uid) != -1;
    }

    @Override
    public List<User> getUser() {
        return DataManager.get().userList;
    }


}
