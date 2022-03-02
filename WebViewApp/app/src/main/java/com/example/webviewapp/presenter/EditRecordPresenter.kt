package com.example.webviewapp.presenter

import com.example.webviewapp.contract.EditRecordContract
import com.example.webviewapp.data.DataManager
import com.example.webviewapp.data.Record

class EditRecordPresenter(private val view: EditRecordContract.View) : EditRecordContract.Presenter {
    override fun checkContent(title: String?, details: String?) {
        view.setConfirmButtonVisibility(title != null && details != null)
    }

    override fun getData(primaryKey: Long): Record? {
        return DataManager.get().queryRecordByPrimaryKey(primaryKey)
    }

    override fun updateRecord(record: Record?) {
        DataManager.get().updateRecord(record)
    }

}