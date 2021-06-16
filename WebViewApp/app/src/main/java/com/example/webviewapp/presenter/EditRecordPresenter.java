package com.example.webviewapp.presenter;

import com.example.webviewapp.contract.EditRecordContract;
import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;

public class EditRecordPresenter implements EditRecordContract.Presenter {
    private final EditRecordContract.View view;

    public EditRecordPresenter(EditRecordContract.View view) {
        this.view = view;
    }

    @Override
    public void checkContent(String title, String details) {
        view.setConfirmButtonVisibility(title != null && details != null);
    }

    @Override
    public Record getData(long primaryKey) {
        return DataManager.get().queryRecordByPrimaryKey(primaryKey);
    }

    @Override
    public void updateRecord(Record record) {
        DataManager.get().updateRecord(record);
    }
}
