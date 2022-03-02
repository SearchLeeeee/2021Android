package com.example.webviewapp.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.common.base.BaseApplication
import com.example.webviewapp.common.utils.DensityUtils
import com.example.webviewapp.common.utils.EventUtils
import com.example.webviewapp.common.utils.EventUtils.TimeEvent
import com.example.webviewapp.common.utils.NetworkUtils
import com.example.webviewapp.databinding.ActivityVideoViewBinding
import com.example.webviewapp.ui.activity.VideoViewActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class VideoViewActivity : BaseActivity() {
    var viewBinding: ActivityVideoViewBinding? = null
    private val deltaTime = 2500
    private var mGestureDetector: GestureDetector? = null
    private var mAudioManager: AudioManager? = null
    private var mStreamVolume = 0
    private var mPlayingPos = 0
    private var mNetworkState = 0 //当前网络状态 0-不可用 1-wifi 2-mobile
    private var mNetworkReceiver: BroadcastReceiver? = null
    private var mLastLoadLength = -1 // 断网 / onStop前缓存的位置信息(ms)
    private var mVideoUri: Uri? = null
    private var mCheckPlayingProgressTimer: Timer? = null
    private var timer: CountDownTimer? = null
    private val mGestureListener: GestureDetector.OnGestureListener = object : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            viewBinding!!.btnPlay.visibility = View.INVISIBLE
            viewBinding!!.btnChange.visibility = View.INVISIBLE
            viewBinding!!.btnSeek.visibility = View.INVISIBLE
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val v = e2.y - e1.y
            Log.d(VIDEO_TAG, "v = $v")
            if (Math.abs(v) > 5) {
//                setScreenBrightness(v);
                setVoiceVolume(v)
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            viewBinding!!.btnPlay.visibility = View.VISIBLE
            viewBinding!!.btnChange.visibility = View.VISIBLE
            viewBinding!!.btnSeek.visibility = View.VISIBLE
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventUtils.register(this)
        initData()
        initVideoView()
        initListener()
    }

    private fun initData() {
        try {
            val screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            }
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setVoiceVolume(value: Float) {
        viewBinding!!.voice.visibility = View.VISIBLE
        var currentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val flag = if (value > 0) -1 else 1
        (currentVolume += flag * 0.1 * maxVolume).toInt()
        // 对currentVolume进行限制
        mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
        if (currentVolume * 100 / maxVolume >= 0 &&
                currentVolume * 100 / maxVolume <= 100) {
            viewBinding!!.voiceText.setText(currentVolume * 100 / maxVolume.toString() + "%")
        }
        if (timer == null) {
            timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    EventUtils.post(TimeEvent())
                }
            }
        }
        timer!!.start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTimeEvent(event: TimeEvent?) {
        viewBinding!!.voice.visibility = View.GONE
    }

    private fun initVideoView() {
        mVideoUri = Uri.parse(getIntent().getStringExtra("video"))
        viewBinding!!.videoView.setVideoURI(mVideoUri)

        //添加播放控制条并设置视频源
        val mediaController = MediaController(this)
        viewBinding!!.videoView.setMediaController(mediaController)
        mediaController.setMediaPlayer(viewBinding!!.videoView)
        mLastLoadLength = -1
        mPlayingPos = -1
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager?
        mStreamVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        mGestureDetector = GestureDetector(this, mGestureListener)
        viewBinding!!.videoViewContainer.setOnTouchListener { v: View?, event: MotionEvent? -> mGestureDetector!!.onTouchEvent(event) }
        registerNetworkReceiver()
    }

    private fun initListener() {
        viewBinding!!.videoView.setOnPreparedListener { }
        viewBinding!!.videoView.setOnErrorListener { mp, what, extra ->
            if (SCHEME_HTTP.equals(mVideoUri!!.scheme, ignoreCase = true) && mNetworkState == 0) {
                Toast.makeText(BaseApplication.getInstance(), "播放时发生错误", Toast.LENGTH_SHORT).show()
            } else {
                restartPlayVideo()
            }
            true
        }
        viewBinding!!.videoView.setOnCompletionListener { Toast.makeText(this@VideoViewActivity, "播放结束", Toast.LENGTH_SHORT).show() }
        viewBinding!!.btnPlay.setOnClickListener { View: View? ->
            if (mNetworkState == 0) {
                if (SCHEME_HTTP.equals(mVideoUri!!.scheme, ignoreCase = true)) {
                    if (mPlayingPos >= mLastLoadLength - deltaTime) {
                        Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }
            viewBinding!!.videoView.start()
            viewBinding!!.btnPlay.visibility = android.view.View.INVISIBLE
            viewBinding!!.btnChange.visibility = android.view.View.INVISIBLE
            viewBinding!!.btnSeek.visibility = android.view.View.INVISIBLE
        }
        viewBinding!!.btnSeek.setOnClickListener { view: View? ->
            val targetPos = 0
            viewBinding!!.videoView.seekTo(targetPos)
            Toast.makeText(this, "已帮您回到开头", Toast.LENGTH_SHORT).show()
        }
        viewBinding!!.btnChange.setOnClickListener { View: View? ->
            if (getRequestedOrientation() === ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        }
        viewBinding!!.backBtn.setOnClickListener { v: View? -> finish() }
    }

    /**
     * 监听网络变化,用于重新缓存
     */
    private fun registerNetworkReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    if (SCHEME_HTTP.equals(mVideoUri!!.scheme, ignoreCase = true)
                            && action.equals(ConnectivityManager.CONNECTIVITY_ACTION, ignoreCase = true)) {
                        doWhenNetworkChange()
                    }
                }
            }
        }
        registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    /**
     * 网络播放进行缓存
     */
    fun doWhenNetworkChange() {
        mNetworkState = NetworkUtils.getNetworkType(this)
        //保存当前已缓存长度
        val bufferPercentage = viewBinding!!.videoView.bufferPercentage
        mLastLoadLength = bufferPercentage * viewBinding!!.videoView.duration / 100
        //这里需要判断 0
        val currentPosition = viewBinding!!.videoView.currentPosition
        if (currentPosition > 0) {
            mPlayingPos = currentPosition
        }
        if (mNetworkState == NetworkUtils.NETWORK_TYPE_INVALID && bufferPercentage < 100) {
            // 监听当前播放位置,在达到缓冲长度前自动停止
            if (mCheckPlayingProgressTimer == null) {
                mCheckPlayingProgressTimer = Timer()
            }
            mCheckPlayingProgressTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    if (mPlayingPos >= mLastLoadLength - deltaTime) {
                        viewBinding!!.videoView.pause()
                    }
                }
            }, 0, 1000) //每秒检测一次
        } else {
            restartPlayVideo()
        }
    }

    private fun unregisterNetworkReceiver() {
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver)
        }
    }

    private fun restartPlayVideo() {
        if (mCheckPlayingProgressTimer != null) {
            mCheckPlayingProgressTimer!!.cancel()
            mCheckPlayingProgressTimer = null
        }
        viewBinding!!.videoView.setVideoURI(mVideoUri)
        viewBinding!!.videoView.start()
        viewBinding!!.videoView.seekTo(mPlayingPos)
        mLastLoadLength = -1
        mPlayingPos = 0
    }

    fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (null == viewBinding!!.videoView) {
            return
        }
        if (this.getResources().getConfiguration().orientation === Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            getWindow().getDecorView().invalidate()
            val height = DensityUtils.getWidthInPx(this)
            val width = DensityUtils.getHeightInPx(this)
            viewBinding!!.videoViewContainer.layoutParams.height = width.toInt()
            viewBinding!!.videoViewContainer.layoutParams.width = height.toInt()
        } else {
            val attrs: WindowManager.LayoutParams = getWindow().getAttributes()
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            getWindow().setAttributes(attrs)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            val width = DensityUtils.getWidthInPx(this)
            val height = DensityUtils.dip2px(this, 200f).toFloat()
            viewBinding!!.videoViewContainer.layoutParams.height = height.toInt()
            viewBinding!!.videoViewContainer.layoutParams.width = width.toInt()
        }
    }

    protected fun onResume() {
        super.onResume()
        mNetworkState = NetworkUtils.getNetworkType(this)
        Log.d(TAG, "onResume: $mPlayingPos")
        //播放网络视频时,需要检测判断网络状态变化
        if (SCHEME_HTTP.equals(mVideoUri!!.scheme, ignoreCase = true) && mNetworkState == 0) {
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
        } else {
            if (mPlayingPos > 0) {
                viewBinding!!.videoView.start()
                viewBinding!!.videoView.seekTo(mPlayingPos)
                mPlayingPos = 0
                mLastLoadLength = -1
            }
        }
    }

    protected fun onPause() {
        mPlayingPos = viewBinding!!.videoView.currentPosition
        viewBinding!!.videoView.pause()
        super.onPause()
    }

    protected fun onStop() {
        if (viewBinding!!.videoView.isPlaying || viewBinding!!.videoView.canPause()) {
            viewBinding!!.videoView.stopPlayback()
        }
        mLastLoadLength = 0
        super.onStop()
    }

    protected fun onDestroy() {
        super.onDestroy()
        if (mCheckPlayingProgressTimer != null) {
            mCheckPlayingProgressTimer!!.cancel()
            mCheckPlayingProgressTimer = null
        }
        unregisterNetworkReceiver()
        EventUtils.unregister(this)
    }

    fun onBackPressed() {
        if (getResources().getConfiguration().orientation === Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        } else {
            finish()
        }
    }

    companion object {
        private const val TAG = "VideoViewActivity"
        private const val VIDEO_TAG = "video_sample"
        private const val SCHEME_HTTP = "https"
    }
}