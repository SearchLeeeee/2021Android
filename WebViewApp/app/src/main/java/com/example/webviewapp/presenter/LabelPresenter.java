package com.example.webviewapp.presenter;

import com.example.webviewapp.contract.LabelContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.List;

public class LabelPresenter implements LabelContract.Presenter {
    private final LabelContract.View view;

    public LabelPresenter(LabelContract.View view) {
        this.view = view;
    }

    @Override
    public List<Record> getData() {
        return DataManager.get().labelList;
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
}
