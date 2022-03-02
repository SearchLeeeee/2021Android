package com.example.webviewapp.presenter

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.webviewapp.contract.LabelContract
import com.example.webviewapp.data.DataManager
import com.example.webviewapp.data.Record

class LabelPresenter(private val view: LabelContract.View) : LabelContract.Presenter {
    override val data: List<Record>
        get() = DataManager.get().labelList

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
        DataManager.get().loadLabels()
        return DataManager.get().labelList
    }

    override fun deleteLabel(pri: List<String?>?) {
        if (pri!!.size == 0) return
        for (i in pri.indices) {
            DataManager.get().deleteRecordsByUrl(pri[i], IS_LABEL)
        }
    }

    companion object {
        const val IS_LABEL = 2
    }

}