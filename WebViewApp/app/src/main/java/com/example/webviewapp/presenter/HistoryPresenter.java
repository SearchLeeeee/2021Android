package com.example.webviewapp.presenter;

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
}
