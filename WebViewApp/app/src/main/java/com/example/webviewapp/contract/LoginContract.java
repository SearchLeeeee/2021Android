package com.example.webviewapp.contract;

public interface LoginContract {
    interface View {

    }

    interface Presenter {
        void Login(String  uid);

        Boolean isLogin();

        void logout();

        void loadRecord(String uid) throws InterruptedException;
    }
}
