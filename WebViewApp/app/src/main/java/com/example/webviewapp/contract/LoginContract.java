package com.example.webviewapp.contract;

import android.text.Editable;


public interface LoginContract {
    interface View {

    }

    interface Presenter {
        Boolean Login(Editable uid, Editable password);
    }
}
