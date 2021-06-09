package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

import java.util.List;

public interface LabelContract {
    interface View {

    }

    interface Presenter {
        List<Record> getData();
    }
}
