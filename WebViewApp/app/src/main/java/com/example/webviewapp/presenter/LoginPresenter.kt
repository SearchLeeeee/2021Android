package com.example.webviewapp.presenter

import android.util.Log
import com.example.webviewapp.common.utils.Cloud.CloudUser
import com.example.webviewapp.contract.LoginContract
import com.example.webviewapp.data.DataManager

class LoginPresenter : LoginContract.Presenter {
    override fun login(uid: String?) {
        DataManager.get().isLogin = true
        DataManager.get().LoginUserId = uid
        Log.d(TAG, "Login: $uid")
        DataManager.get().WriteLoginState(DataManager.get().LoginUserId, DataManager.get().isLogin)
    }

    override val isLogin: Boolean
        get() = DataManager.get().getIsLogin()

    override fun logout() {
        val records = DataManager.get().queryALLRecord()
        val uid = DataManager.get().LoginUserId
        Log.d(TAG, " logout: $uid")
        CloudUser.get().uploadRecord(uid, records)
        DataManager.get().isLogin = false
        DataManager.get().LoginUserId = "100"
        DataManager.get().WriteLoginState(DataManager.get().LoginUserId, DataManager.get().isLogin)
    }

    @Throws(InterruptedException::class)
    override fun loadRecord(uid: String?) {
        val records = CloudUser.get().getRecordsCloud(uid)
        for (i in records.indices) {
            Log.d(TAG, "loadRecord: " + records[i].title)
            if (DataManager.get().queryRecordTitleByUrl(records[i].url, records[i].isHistory) != null) DataManager.get().deleteRecordsByUrl(records[i].url, records[i].isHistory)
            DataManager.get().addRecord(records[i])
        }
    }

    companion object {
        private const val TAG = "LoginPresenter"
    }
}