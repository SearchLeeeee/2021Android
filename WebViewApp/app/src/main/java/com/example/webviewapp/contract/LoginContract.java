package com.example.webviewapp.contract;

public interface LoginContract {
    interface View {

    }

    interface Presenter {
        void Login();

        Boolean isLogin();

        void logout();
    }
}
