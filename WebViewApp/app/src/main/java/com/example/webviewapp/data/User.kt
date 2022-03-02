package com.example.webviewapp.data

class User {
    var email: String? = null
    var avatarId = 0

    constructor() {}
    constructor(Email: String?, avatarId: Int) {
        email = Email
        this.avatarId = avatarId
    }

}