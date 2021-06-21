package com.example.webviewapp.contract;

import com.example.webviewapp.data.Record;

public interface EditRecordContract {

    interface View {
        void setConfirmButtonVisibility(Boolean isVisible);
    }

    interface Presenter {
        void checkContent(String title, String details);

        Record getData(long primaryKey);

        void updateRecord(Record record);
    }
}
