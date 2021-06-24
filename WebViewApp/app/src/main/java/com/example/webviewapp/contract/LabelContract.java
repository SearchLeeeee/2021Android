package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

import java.util.List;

public interface LabelContract {
    interface View {
        void setEditTextVisibility(Boolean isVisible);
    }

    interface Presenter {
        List<Record> getData();

        void checkScrolled(int dy);

        List<Record> refreshRecord();
    }
}
