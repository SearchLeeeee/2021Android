package com.example.webviewapp.contract

interface LoginContract {
    interface View
    interface Presenter {
        fun Login(uid: String?)
        val isLogin: Boolean?
        fun logout()

        @Throws(InterruptedException::class)
        fun loadRecord(uid: String?)
    }
}