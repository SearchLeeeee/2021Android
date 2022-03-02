package com.example.webviewapp.presenter

import com.example.webviewapp.contract.MainContract
import com.example.webviewapp.data.DataManager
import com.example.webviewapp.data.Record
import java.util.*

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {
    override fun addHistory(url: String?, title: String?) {
        if (title == "") return
        val record = Record(100, System.currentTimeMillis(), url, title, "test", IS_HISTORY)
        if (DataManager.get().queryRecordTitleByUrl(url, IS_HISTORY) != null) DataManager.get().deleteRecordsByUrl(url, IS_HISTORY)
        DataManager.get().addRecord(record)
    }

    //获取即使记录
    override val history: List<String>
        get() {
            //获取即使记录
            val note = DataManager.get().queryAllHistoryRecord()
            val history: MutableList<String> = ArrayList()
            val hm = HashMap<String?, Int?>()
            var temp: String
            for (i in note.indices) {
                if (!hm.containsKey(note[i].title)) {
                    hm[note[i].title] = i
                    if (note[i].title!!.contains("-百度")) {
                        temp = note[i].title!!.replace("- 百度".toRegex(), "")
                        history.add(temp)
                    }
                }
            }
            return history
        }

    override fun addLabel(url: String?, title: String?) {
        val record = Record(100, System.currentTimeMillis(), url, title, "test", IS_LABEL)
        if (DataManager.get().queryRecordTitleByUrl(url, IS_LABEL) != null) DataManager.get().deleteRecordsByUrl(url, IS_LABEL)
        DataManager.get().addRecord(record)
    }

    override val labelUrl: List<String?>
        get() {
            val note = DataManager.get().labelList
            val label: MutableList<String?> = ArrayList()
            val hm = HashMap<String?, Int?>()
            for (i in note.indices) {
                if (!hm.containsKey(note[i].url)) {
                    hm[note[i].url] = i
                    label.add(note[i].url)
                }
            }
            return label
        }

    override val historyRecord: List<Record>
        get() = DataManager.get().historyList

    override fun deleteRecord(url: String?) {
        DataManager.get().deleteRecordsByUrl(url, IS_LABEL)
    }

    companion object {
        private const val TAG = "MainPresenter"
        const val IS_HISTORY = 1
        const val IS_LABEL = 2
    }

}