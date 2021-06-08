package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

import java.util.List;

public interface HistoryContract {
    interface View {

    }

    interface Presenter {
        public List<Record> getData();
    }
}
