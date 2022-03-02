package com.example.webviewapp.presenter

import android.text.Editable
import android.util.Log
import com.example.webviewapp.contract.SignUpContract
import com.example.webviewapp.data.DataManager
import com.example.webviewapp.data.User

class SignUpPresenter : SignUpContract.Presenter {
    override fun signUp() {
        Log.d("TAG", "SignUp:  succssed")
    }

    override fun containsUid(uidText: Editable?): Boolean? {
        if (uidText.toString() == "") return true
        val uid = uidText.toString().toLong()
        //        return DataManager.get().queryUserPasswordByUid(uid) != -1;
        return true
    }

    override val user: List<User>
        get() = DataManager.get().userList
}