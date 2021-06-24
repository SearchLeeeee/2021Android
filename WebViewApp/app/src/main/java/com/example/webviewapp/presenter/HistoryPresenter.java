package com.example.webviewapp.presenter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.webviewapp.contract.HistoryContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.List;

public class HistoryPresenter implements HistoryContract.Presenter {
    private final HistoryContract.View view;

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
        DataManager.get().loadLabels();
        return DataManager.get().labelList;
    }
}
