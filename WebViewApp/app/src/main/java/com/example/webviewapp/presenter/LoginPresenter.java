package com.example.webviewapp.presenter;

import com.example.webviewapp.contract.LoginContract;
import com.example.webviewapp.data.DataManager;


public class LoginPresenter implements LoginContract.Presenter {


    @Override
    public void Login() {
        // 判断输入是否为空再进行toString转换
//        if (!uidText.toString().equals("") && !passwordText.toString().equals("")) {
//            long uid = Long.parseLong(uidText.toString());
//            long password = Long.parseLong(passwordText.toString());
//            return DataManager.get().queryUserPasswordByUid(uid) == password;
//        } else return false;
        DataManager.get().isLogin = true;
    }

    @Override
    public Boolean isLogin() {
        return DataManager.get().isLogin;
    }


}
