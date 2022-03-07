package com.example.webviewapp.contract

import com.example.webviewapp.data.Record

interface MainContract {
    interface View
    interface Presenter {
        fun addHistory(url: String?, title: String?)
        val history: List<String?>?
        fun addLabel(url: String?, title: String?)
        val labelUrl: List<String?>?
        val historyRecord: List<Record?>?
        fun deleteRecord(url: String?)
    }
}