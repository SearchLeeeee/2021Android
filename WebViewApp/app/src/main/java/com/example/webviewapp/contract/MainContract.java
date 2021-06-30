package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

import java.util.List;

public interface MainContract {
    interface View {

    }

    interface Presenter {
        void addHistory(String url, String title);

        List<String> getHistory();

        void addLabel(String url, String title);

        List<String> getLabelUrl();

        List<Record> getHistoryRecord();
    }
}
