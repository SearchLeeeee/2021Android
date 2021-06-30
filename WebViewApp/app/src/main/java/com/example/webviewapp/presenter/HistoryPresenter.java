package com.example.webviewapp.presenter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.webviewapp.contract.HistoryContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.List;

public class HistoryPresenter implements HistoryContract.Presenter {
    private final HistoryContract.View view;
    public static final int IS_HISTORY = 1;
    public HistoryPresenter(HistoryContract.View view) {
        this.view = view;
    }

    @Override
    public List<Record> getData() {
        return DataManager.get().historyList;
    }


    @Override
    public void checkScrolled(int dy) {
        if (dy > 20) {
            view.setEditTextVisibility(false);
        }
        if (dy < 0) {
            view.setEditTextVisibility(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Record> refreshRecord() {
        DataManager.get().loadHistories();
        return DataManager.get().historyList;
    }

    @Override
    public void deleteAllHistory() {
        DataManager.get().deleteAllRecords(DataManager.IS_HISTORY);
    }

    @Override
    public void deleteHistoryByUrl(List<String> url) {
        for (int i = 0; i < url.size(); i++)
            DataManager.get().deleteRecordsByUrl(url.get(i), IS_HISTORY);
    }
}
