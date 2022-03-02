package com.example.webviewapp.contract

import com.example.webviewapp.data.Record

interface LabelContract {
    interface View {
        fun setEditTextVisibility(isVisible: Boolean?)
    }

    interface Presenter {
        val data: List<Record?>?
        fun checkScrolled(dy: Int)
        fun refreshRecord(): List<Record?>?
        fun deleteLabel(pri: List<String?>?)
    }
}