package com.example.webviewapp.presenter;

import com.example.webviewapp.contract.MainContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";
    public static final int IS_HISTORY = 1;
    public static final int IS_LABEL = 2;
    private final MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void addHistory(String url, String title) {
        if (title.equals("")) return;
        Record record = new Record(100, System.currentTimeMillis(), url, title, "test", IS_HISTORY);
        if (DataManager.get().queryRecordTitleByUrl(url, IS_HISTORY) != null)
            DataManager.get().deleteRecordsByUrl(url, IS_HISTORY);
        DataManager.get().addRecord(record);
    }

    @Override
    public List<String> getHistory() {
        List<Record> note = DataManager.get().historyList;
        List<String> history = new ArrayList<>();
        HashMap<String, Integer> hm = new HashMap<>();
        String temp;
        for (int i = 0; i < note.size(); i++) {
            if (!hm.containsKey(note.get(i).getTitle())) {
                hm.put(note.get(i).getTitle(), i);
                if (note.get(i).getTitle().contains("百度")) {
                    temp = note.get(i).getTitle().replaceAll("百度", "");
                    history.add(temp);
                }
            }
        }

        return history;
    }

    @Override
    public void addLabel(String url, String title) {
        Record record = new Record(100, System.currentTimeMillis(), url, title, "test", IS_LABEL);
        if (DataManager.get().queryRecordTitleByUrl(url, IS_LABEL) != null)
            DataManager.get().deleteRecordsByUrl(url, IS_LABEL);
        DataManager.get().addRecord(record);
    }

    @Override
    public List<String> getLabelUrl() {
        List<Record> note = DataManager.get().labelList;
        List<String> label = new ArrayList<>();
        HashMap<String, Integer> hm = new HashMap<>();
        for (int i = 0; i < note.size(); i++) {
            if (!hm.containsKey(note.get(i).getUrl())) {
                hm.put(note.get(i).getUrl(), i);
                label.add(note.get(i).getUrl());
            }
        }
        return label;
    }

    @Override
    public List<Record> getHistoryRecord() {
        return DataManager.get().historyList;
    }

    @Override
    public void deleteRecord(String url) {
        DataManager.get().deleteRecordsByUrl(url, IS_LABEL);
    }


}
