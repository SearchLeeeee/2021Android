package com.example.webviewapp.data

import java.util.*

class NewsItem {
    var uniquekey: String? = null
    var title: String? = null
    var date: String? = null
    var category: String? = null
    var authorName: String? = null
    var url: String? = null
    var thumbnailPics: List<String> = ArrayList()
    var isContent: String? = null

    constructor() {}
    constructor(uniquekey: String?, title: String?, date: String?, category: String?, authorName: String?, url: String?, thumbnailPics: List<String>, isContent: String?) {
        this.uniquekey = uniquekey
        this.title = title
        this.date = date
        this.category = category
        this.authorName = authorName
        this.url = url
        this.thumbnailPics = thumbnailPics
        this.isContent = isContent
    }

}