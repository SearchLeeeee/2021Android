package com.example.webviewapp.common.base

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.webviewapp.common.utils.AdBlocker
import com.example.webviewapp.data.DataManager

/**
 * 获取全局context、做一些启动前的初始化操作
 */
class BaseApplication : Application() {
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        instance = this
        initApplication()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun initApplication() {
        DataManager.init(instance)
        AdBlocker.init(instance)
    }

    companion object {
        var instance: BaseApplication? = null
            private set
    }
}