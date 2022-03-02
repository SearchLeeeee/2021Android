package com.example.webviewapp.contract

import android.text.Editable
import com.example.webviewapp.data.User

interface SignUpContract {
    interface View
    interface Presenter {
        fun signUp()
        fun containsUid(uidText: Editable?): Boolean?
        val user: List<User?>?
    }
}