package com.example.webviewapp.presenter;

import android.util.Log;

import com.example.webviewapp.contract.MainContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainContract.Presenter {

    public static final int IS_HISTORY = 1;


    @Override
    public void addHistory(String url, String title) {
        Record record = new Record(100, System.currentTimeMillis(), url, title, "test", IS_HISTORY);
        DataManager.get().addRecord(record);
        Log.d("TAG", "addHistory: " + DataManager.get().historyList.get(0).getTitle());
    }

    @Override
    public List<String> getHistory() {
        List<Record> note = DataManager.get().historyList;
        List<String> history = new ArrayList<>();
        for (int i = 0; i < note.size(); i++) {
            history.add(note.get(i).getTitle());
        }
        return history;
    }
}
