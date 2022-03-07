package com.example.webviewapp.contract

import com.example.webviewapp.data.Record

interface HistoryContract {
    interface View {
        fun setEditTextVisibility(isVisible: Boolean?)
    }

    interface Presenter {
        val data: List<Record?>?
        fun refreshRecord(): List<Record?>?
        fun checkScrolled(dy: Int)
        fun deleteAllHistory()
        fun deleteHistoryByUrl(url: List<String?>?)
    }
}