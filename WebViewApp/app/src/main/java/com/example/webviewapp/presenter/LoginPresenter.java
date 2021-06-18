package com.example.webviewapp.presenter;

import android.text.Editable;

import com.example.webviewapp.contract.LoginContract;
import com.example.webviewapp.data.DataManager;


public class LoginPresenter implements LoginContract.Presenter {


    @Override
    public Boolean Login(Editable uidText, Editable passwordText) {
        long uid = Long.parseLong(uidText.toString());
        long password = Long.parseLong(passwordText.toString());
        return DataManager.get().queryUserPasswordByUid(uid) == password;
    }
}
