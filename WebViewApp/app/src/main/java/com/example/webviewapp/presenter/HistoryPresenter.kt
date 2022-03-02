package com.example.webviewapp.presenter

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.webviewapp.contract.HistoryContract
import com.example.webviewapp.data.DataManager
import com.example.webviewapp.data.Record

class HistoryPresenter(private val view: HistoryContract.View) : HistoryContract.Presenter {
    override val data: List<Record>
        get() = DataManager.get().historyList

    override fun checkScrolled(dy: Int) {
        if (dy > 20) {
            view.setEditTextVisibility(false)
        }
        if (dy < 0) {
            view.setEditTextVisibility(true)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun refreshRecord(): List<Record?>? {
        DataManager.get().loadHistories()
        return DataManager.get().historyList
    }

    override fun deleteAllHistory() {
        DataManager.get().deleteAllRecords(DataManager.IS_HISTORY)
    }

    override fun deleteHistoryByUrl(url: List<String?>?) {
        for (i in url!!.indices) DataManager.get().deleteRecordsByUrl(url[i], IS_HISTORY)
    }

    companion object {
        const val IS_HISTORY = 1
    }

}