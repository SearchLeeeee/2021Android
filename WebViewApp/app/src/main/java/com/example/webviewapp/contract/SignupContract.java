package com.example.webviewapp.contract;

import android.text.Editable;

import com.example.webviewapp.data.User;

import java.util.List;

public interface SignupContract {

    interface View {

    }

    interface Presenter {
        void SignUp(Editable uidText, Editable passwordText);

        Boolean ContainsUid(Editable uidtext);

        List<User> getUser();
    }
}
