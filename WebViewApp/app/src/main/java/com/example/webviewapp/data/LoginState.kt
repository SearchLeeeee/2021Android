package com.example.webviewapp.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LoginState : RealmObject, Parcelable {
    var loginId: String? = null
    var login = false

    @PrimaryKey
    var primaryKey: Long = 0

    constructor() {}
    constructor(LoginId: String?, isLogin: Boolean) {
        login = isLogin
        loginId = LoginId
    }

    protected constructor(input: Parcel) {
        loginId = input.readString()
        login = input.readByte().toInt() != 0
        primaryKey = input.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(loginId)
        dest.writeByte((if (login) 1 else 0).toByte())
        dest.writeLong(primaryKey)
    }

    companion object {
        val CREATOR: Creator<LoginState?> = object : Creator<LoginState?> {
            override fun createFromParcel(`in`: Parcel): LoginState? {
                return LoginState(`in`)
            }

            override fun newArray(size: Int): Array<LoginState?> {
                return arrayOfNulls(size)
            }
        }
    }
}