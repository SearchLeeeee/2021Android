package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

import java.util.List;

public interface HistoryContract {
    interface View {
        void setEditTextVisibility(Boolean isVisible);
    }

    interface Presenter {
        List<Record> getData();

        List<Record> refreshRecord();

        void checkScrolled(int dy);
    }
}
