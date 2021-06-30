package com.example.webviewapp.presenter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.webviewapp.contract.LabelContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

import java.util.List;

public class LabelPresenter implements LabelContract.Presenter {
    private final LabelContract.View view;
    public static final int IS_LABEL = 2;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Record> refreshRecord() {
        DataManager.get().loadLabels();
        return DataManager.get().labelList;
    }

    @Override
    public void deleteLabel(List<String> pri) {
        if (pri.size() == 0) return;
        for (int i = 0; i < pri.size(); i++) {
            DataManager.get().deleteRecordsByUrl(pri.get(i), IS_LABEL);
        }
    }


}
