package com.example.webviewapp.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Permissions : RealmObject {
    @PrimaryKey
    var permission: String? = null
    var time: Long = 0

    constructor() {}

    constructor(permission: String?, time: Long) {
        this.permission = permission
        this.time = time
    }
}