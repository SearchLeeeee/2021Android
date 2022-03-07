package com.example.webviewapp.contract

import com.example.webviewapp.data.Record

interface EditRecordContract {
    interface View {
        fun setConfirmButtonVisibility(isVisible: Boolean?)
    }

    interface Presenter {
        fun checkContent(title: String?, details: String?)
        fun getData(primaryKey: Long): Record?
        fun updateRecord(record: Record?)
    }
}