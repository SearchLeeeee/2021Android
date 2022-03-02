package com.example.webviewapp.common.base

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.webviewapp.common.utils.UIUtils
import com.example.webviewapp.data.DataManager

/**
 * 继承后可以简化ViewBinding的使用，只需要在实现类中声明即可
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        supportActionBar!!.hide()
    }
    // region 初始化
    /**
     * 初始化
     */
    protected fun setupViews() {
        try {
            setupViewBinding()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 配置View绑定
     * @throws Exception
     */
    @Throws(Exception::class)
    fun setupViewBinding() {
        val clazz: Class<out BaseActivity?> = javaClass
        val vbClazz: Class<out ViewBinding> = ViewBinding::class.java
        val fields = clazz.fields
        for (field in fields) {
            val fClazz = field.type
            // 如果是 ViewBinding 子类
            if (vbClazz.isAssignableFrom(fClazz)) {
                val inflate = fClazz.getMethod("inflate", LayoutInflater::class.java)
                val vb = inflate.invoke(null, layoutInflater) as ViewBinding
                setContentView(vb.root)
                field[this] = vb
            }
        }
    }

    /**
     * 封装好点击一个view跳转到一个activity的动作
     *
     * @param view  被点击的view
     * @param clazz 目标activity
     * @return
     */
    protected fun registerActivitySwitch(view: View?, clazz: Class<out Activity?>?): Intent {
        return UIUtils.registerActivitySwitch(this, view, clazz)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == OPEN_SET_REQUEST_CODE) for (i in permissions.indices) if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
            // 申请成功
            successRequestPermission(permissions[i])
        } else {
            // 申请失败
            failRequestPermission(permissions[i])
        }
    }

    protected fun failRequestPermission(permission: String) {
        Log.d(TAG, "checkPermission: [权限]$permission 申请失败")
        Toast.makeText(this, "[权限]$permission 申请失败", Toast.LENGTH_SHORT).show()
        DataManager.get().addDeniedPermission(permission)
    }

    protected fun successRequestPermission(permission: String) {
        Log.d(TAG, "checkPermission: [权限]$permission 申请成功")
        DataManager.get().deleteDeniedPermission(permission)
    }

    companion object {
        private const val TAG = "BaseActivity"
        const val OPEN_SET_REQUEST_CODE = 100
    }
}