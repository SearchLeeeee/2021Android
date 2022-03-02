package com.example.webviewapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.webviewapp.R
import com.example.webviewapp.common.adapters.CustomWebViewClient
import com.example.webviewapp.common.adapters.JavaScripInterfaceAdapter
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.common.utils.PermissionUtils.PermissionsManager
import com.example.webviewapp.contract.MainContract
import com.example.webviewapp.databinding.ActivityMainBinding
import com.example.webviewapp.presenter.MainPresenter
import com.example.webviewapp.ui.activity.MainActivity

class MainActivity : BaseActivity(), MainContract.View {
    private var presenter: MainContract.Presenter? = null
    var viewBinding: ActivityMainBinding? = null
    private var popWindow: PopupWindow? = null
    private var view: View? = null
    private var addedText: TextView? = null
    private var addLabel: ImageView? = null
    var webViewClient: CustomWebViewClient = object : CustomWebViewClient(presenter) {
        override fun onPageFinished(view: WebView, url: String) { //页面加载完成
            presenter!!.addHistory(url, view.title)
            viewBinding!!.progressbar.visibility = View.GONE
            initUrl(view)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) { //页面开始加载
            viewBinding!!.progressbar.visibility = View.VISIBLE
            if (presenter!!.labelUrl!!.contains(viewBinding!!.webview.url)) {
                addedText!!.text = "已添加"
                addLabel!!.setImageResource(R.drawable.collected)
            } else {
                addedText!!.text = "添加"
                addLabel!!.setImageResource(R.drawable.uncollected)
            }
        }
    }

    private fun initUrl(view: WebView) {
        view.loadUrl("javascript:(function()" +
                "    {" +
                "        var objs = document.getElementsByTagName('img');" +
                "        var array=new Array();" +
                "        for(var j=0;j<objs.length;j++)" +
                "            {" +
                "                array[j]=objs[j].src;" +
                "            }" +
                "        for(var i=0;i<objs.length;i++)" +
                "            {" +
                "                objs[i].onclick=function()" +
                "                    {" +
                "                        window.imagelistener.openImage(this.src,array);" +
                "                    }" +
                "            }" +
                "    })()")
        view.loadUrl("javascript:(function()" +
                "    {" +
                "        var objs = document.getElementsByTagName('video');" +
                "        for(var i=0;i<objs.length;i++)" +
                "            {" +
                "                objs[i].addEventListener('play', function () {" +
                "                   window.imagelistener.openVideo(this.currentSrc);" +
                "                   this.pause();" +
                "                });" +
                "            }" +
                "    })()")
    }

    /**
     * WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        //监听js alert弹窗事件
        override fun onJsAlert(webView: WebView, url: String, message: String, result: JsResult): Boolean {
            if (message == "1") {
                Log.i("弹窗", "继续访问:" + webViewClient.blockUrl)
                webView.loadUrl(webViewClient.blockUrl)
            } else if (message == "0") {
                Log.i("弹窗", "停止访问")
                webView.goBack()
                webView.goBack()
            }
            result.confirm()
            return true
        }

        //加载进度回调
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            viewBinding!!.progressbar.progress = newProgress
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        presenter = MainPresenter(this)
        initWebView()
        initButton()
        initSearchBar()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (intent.getStringExtra("url") != null) {
            viewBinding!!.webview.loadUrl(intent.getStringExtra("url")!!)
        }
    }

    private fun initSearchBar() {
        viewBinding!!.searchbar.isIconifiedByDefault = false
        val history = presenter!!.history
        val adapter: ArrayAdapter<*> = ArrayAdapter(this, android.R.layout.simple_list_item_1, history!!)
        viewBinding!!.listView.adapter = adapter
        viewBinding!!.listView.isTextFilterEnabled = true
        viewBinding!!.listView.visibility = View.GONE
        viewBinding!!.listView.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
            val result = parent.getItemAtPosition(position).toString() //获取选择项的值
            val url = "https://wap.baidu.com/s?word=$result"
            viewBinding!!.webview.loadUrl(url)
            viewBinding!!.listView.visibility = View.GONE
        }
        viewBinding!!.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
                val url = "https://wap.baidu.com/s?word=$query"
                viewBinding!!.webview.loadUrl(url)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    viewBinding!!.listView.visibility = View.GONE
                    viewBinding!!.listView.clearTextFilter()
                } else {
                    viewBinding!!.listView.visibility = View.VISIBLE
                    adapter.filter.filter(newText)
                }
                return true
            }
        })
    }

    private fun initButton() {
        //设置按钮的点击事件
        view = LayoutInflater.from(this@MainActivity).inflate(R.layout.menu_mainpage, null, false)
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        initPopWindow()
        viewBinding!!.menuButton.setOnClickListener { v: View? ->
            if (popWindow!!.isShowing) popWindow!!.dismiss() else {
                popWindow!!.showAtLocation(view, Gravity.BOTTOM, 0, viewBinding!!.buttomview.height)
            }
        }
        viewBinding!!.refreshButton.setOnClickListener { v: View? -> viewBinding!!.webview.loadUrl(DEFAULT_URL) }
        viewBinding!!.backButton.setOnClickListener { v: View? ->
            if (viewBinding!!.webview.url == "file:///android_asset/askToJump.html") { //在风险访问h5页面需要两次goback才能回去
                Log.i("TAG", "same")
                viewBinding!!.webview.goBack()
                viewBinding!!.webview.goBack()
            } else if (viewBinding!!.webview.canGoBack()) viewBinding!!.webview.goBack() else onBackPressed()
        }
        viewBinding!!.nextButton.setOnClickListener { v: View? -> viewBinding!!.webview.goForward() }
        viewBinding!!.fowardButton.setOnClickListener { v: View? -> startActivity(Intent(this@MainActivity, InfoReadActivity::class.java)) }
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {
        viewBinding!!.webview.loadUrl("https://www.baidu.com/")
        val webSettings = viewBinding!!.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        viewBinding!!.webview.loadUrl(DEFAULT_URL)
        val javaScripInterface = JavaScripInterfaceAdapter(this)
        viewBinding!!.webview.addJavascriptInterface(javaScripInterface, "imagelistener")
        viewBinding!!.webview.addJavascriptInterface(javaScripInterface, "blockListener")
        viewBinding!!.webview.webViewClient = webViewClient
        viewBinding!!.webview.webChromeClient = webChromeClient
    }

    override fun onDestroy() {
        // 子线程清空磁盘缓存
        Thread(Runnable { Glide.get(this@MainActivity).clearDiskCache() }).start()
        // 主线程清空内存缓存
        Glide.get(this).clearMemory()
        super.onDestroy()
    }

    /**
     * 菜单栏的实现
     */
    @SuppressLint("ClickableViewAccessibility")
    fun initPopWindow() {
        // PopWindow 布局发
        popWindow = PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popWindow!!.isTouchable = true
        popWindow!!.isOutsideTouchable = true
        popWindow!!.animationStyle = R.style.Animation_Design_BottomSheetDialog
        popWindow!!.setTouchInterceptor { v: View?, event: MotionEvent? -> false }
        val userCenter = view!!.findViewById<LinearLayout>(R.id.user_center)
        val quitButton = view!!.findViewById<RelativeLayout>(R.id.quit_button)
        val historyButton = view!!.findViewById<RelativeLayout>(R.id.history_button)
        val refreshButton = view!!.findViewById<RelativeLayout>(R.id.refresh_button)
        val addLabelButton = view!!.findViewById<RelativeLayout>(R.id.add_bookmark_button)
        addLabel = view!!.findViewById(R.id.add_bookmark_image)
        addedText = view!!.findViewById(R.id.add_bookmark_text)
        userCenter.setOnClickListener { v: View? -> startActivity(Intent(this@MainActivity, UserActivity::class.java)) }
        historyButton.setOnClickListener { v: View? ->
            popWindow!!.dismiss()
            startActivity(Intent(this@MainActivity, RecordActivity::class.java))
        }
        addLabelButton.setOnClickListener { v: View? ->
            if (addedText.getText().toString() == "添加") {
                addLabel()
                addedText.setText("已添加")
                addLabel.setImageResource(R.drawable.collected)
            } else {
                deleteLabel()
                addedText.setText("添加")
                addLabel.setImageResource(R.drawable.uncollected)
            }
        }
        quitButton.setOnClickListener { v: View? -> finish() }
        refreshButton.setOnClickListener { v: View? ->
            viewBinding!!.webview.reload()
            Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

    fun addLabel() {
        Toast.makeText(this, "书签添加成功", Toast.LENGTH_SHORT).show()
        presenter!!.addLabel(viewBinding!!.webview.url, viewBinding!!.webview.title)
    }

    fun deleteLabel() {
        Toast.makeText(this, "取消书签收藏", Toast.LENGTH_SHORT).show()
        presenter!!.deleteRecord(viewBinding!!.webview.url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (!viewBinding!!.webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) finish()
        return if (viewBinding!!.webview.url == "file:///android_asset/askToJump.html" && keyCode == KeyEvent.KEYCODE_BACK) {
            viewBinding!!.webview.goBack()
            viewBinding!!.webview.goBack()
            false
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("TAG", "onBackPressed  22222 : 按下了返回键")
            viewBinding!!.webview.goBack()
            false
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun requestPermissions() {
        PermissionsManager(this@MainActivity).requestPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.VIBRATE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        )
    }

    companion object {
        private const val TAG = "MainActivity"
        const val DEFAULT_URL = "file:///android_asset/index.html"
    }
}