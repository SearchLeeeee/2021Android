package com.example.webviewapp.presenter;

import android.util.Log;

import com.example.webviewapp.common.utils.Cloud.CloudUser;
import com.example.webviewapp.contract.LoginContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.List;

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "LoginPresenter";
    @Override
    public void Login(String uid) {
        DataManager.get().isLogin = true;
        DataManager.get().LoginUserId = uid;
        Log.d(TAG, "Login: "+uid);
        DataManager.get().WriteLoginState(DataManager.get().LoginUserId,DataManager.get().isLogin);
    }

    @Override
    public Boolean isLogin() {
        return DataManager.get().getIsLogin();
    }

    @Override
    public void logout() {
        List<Record> records = DataManager.get().queryALLRecord();
        String  uid = DataManager.get().LoginUserId;
        Log.d(TAG, " logout: "+uid);
        CloudUser.get().uploadRecord(uid,records);
        DataManager.get().isLogin = false;
        DataManager.get().LoginUserId = "100";
        DataManager.get().WriteLoginState(DataManager.get().LoginUserId, DataManager.get().isLogin);
    }

    @Override
    public void loadRecord(String uid) throws InterruptedException {
        List<Record> records =  CloudUser.get().getRecordsCloud(uid);
        for (int i=0;i<records.size();i++) {
            Log.d(TAG, "loadRecord: "+ records.get(i).getTitle());
            if (DataManager.get().queryRecordTitleByUrl(records.get(i).getUrl(), records.get(i).getIsHistory()) != null)
                DataManager.get().deleteRecordsByUrl(records.get(i).getUrl(), records.get(i).getIsHistory());
            DataManager.get().addRecord(records.get(i));
        }
    }


}
