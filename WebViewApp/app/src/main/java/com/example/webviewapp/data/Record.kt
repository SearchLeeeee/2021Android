package com.example.webviewapp.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Record : RealmObject, Parcelable {
    /**
     * 表示该字段是主键
     *
     *
     * 字段类型必须是字符串（String）或整数（byte，short，int或long）
     * 以及它们的包装类型（Byte,Short, Integer, 或 Long）。不可以存在多个主键，
     * 使用字符串字段作为主键意味着字段被索引（注释@PrimaryKey隐式地设置注释@Index）。
     */
    @PrimaryKey
    var primaryKey: Long = 0
    var uid: Long = 0
    var time: Long = 0
    var url: String? = null
    var title: String? = null
    var details: String? = null
    var isHistory = 0

    constructor() {}
    constructor(uid: Long) {
        this.uid = uid
    }

    constructor(uid: Long, time: Long, url: String?, title: String?, details: String?, isHistory: Int) {
        this.uid = uid
        this.time = time
        this.url = url
        this.title = title
        this.details = details
        this.isHistory = isHistory
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(primaryKey)
        dest.writeLong(uid)
        dest.writeLong(time)
        dest.writeString(url)
        dest.writeString(title)
        dest.writeString(details)
        dest.writeInt(isHistory)
    }

    protected constructor(`in`: Parcel) {
        primaryKey = `in`.readLong()
        uid = `in`.readLong()
        time = `in`.readLong()
        url = `in`.readString()
        title = `in`.readString()
        details = `in`.readString()
        isHistory = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Creator<Record?> = object : Creator<Record?> {
            override fun createFromParcel(source: Parcel): Record? {
                return Record(source)
            }

            override fun newArray(size: Int): Array<Record?> {
                return arrayOfNulls(size)
            }
        }
    }
}