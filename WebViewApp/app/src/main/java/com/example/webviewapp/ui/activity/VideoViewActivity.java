package com.example.webviewapp.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.common.base.BaseApplication;
import com.example.webviewapp.common.utils.DensityUtils;
import com.example.webviewapp.common.utils.EventUtils;
import com.example.webviewapp.common.utils.NetworkUtils;
import com.example.webviewapp.databinding.ActivityVideoViewBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class VideoViewActivity extends BaseActivity {
    private static final String TAG = "VideoViewActivity";
    public ActivityVideoViewBinding viewBinding;
    private static final String VIDEO_TAG = "video_sample";
    private static final String SCHEME_HTTP = "https";
    private final int deltaTime = 2500;
    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private int mStreamVolume;
    private int mPlayingPos;
    private int mNetworkState = 0;//当前网络状态 0-不可用 1-wifi 2-mobile
    private BroadcastReceiver mNetworkReceiver;
    private int mLastLoadLength = -1;// 断网 / onStop前缓存的位置信息(ms)
    private Uri mVideoUri;
    private Timer mCheckPlayingProgressTimer;
    private CountDownTimer timer;
    private final GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            viewBinding.btnPlay.setVisibility(View.INVISIBLE);
            viewBinding.btnChange.setVisibility(View.INVISIBLE);
            viewBinding.btnSeek.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float v = e2.getY() - e1.getY();
            Log.d(VIDEO_TAG, "v = " + v);
            if (Math.abs(v) > 5) {
//                setScreenBrightness(v);
                setVoiceVolume(v);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            viewBinding.btnPlay.setVisibility(View.VISIBLE);
            viewBinding.btnChange.setVisibility(View.VISIBLE);
            viewBinding.btnSeek.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventUtils.register(this);

        initData();
        initVideoView();
        initListener();
    }

    private void initData() {
        try {
            int screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);

            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setVoiceVolume(float value) {
        viewBinding.voice.setVisibility(View.VISIBLE);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int flag = value > 0 ? -1 : 1;
        currentVolume += flag * 0.1 * maxVolume;
        // 对currentVolume进行限制
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        if (currentVolume * 100 / maxVolume >= 0 &&
                currentVolume * 100 / maxVolume <= 100) {
            viewBinding.voiceText.setText(currentVolume * 100 / maxVolume + "%");
        }
        if (timer == null) {
            timer = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    EventUtils.post(new EventUtils.TimeEvent());
                }
            };
        }
        timer.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(EventUtils.TimeEvent event) {
        viewBinding.voice.setVisibility(View.GONE);
    }

    private void initVideoView() {
        mVideoUri = Uri.parse(getIntent().getStringExtra("video"));
        viewBinding.videoView.setVideoURI(mVideoUri);

        //添加播放控制条并设置视频源
        MediaController mediaController = new MediaController(this);
        viewBinding.videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(viewBinding.videoView);
        mLastLoadLength = -1;
        mPlayingPos = -1;
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mGestureDetector = new GestureDetector(this, mGestureListener);
        viewBinding.videoViewContainer.setOnTouchListener((v, event) ->
                mGestureDetector.onTouchEvent(event));

        registerNetworkReceiver();
    }

    private void initListener() {
        viewBinding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }
        });
        viewBinding.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (SCHEME_HTTP.equalsIgnoreCase(mVideoUri.getScheme()) && mNetworkState == 0) {
                    Toast.makeText(BaseApplication.getInstance(), "播放时发生错误", Toast.LENGTH_SHORT).show();
                } else {
                    restartPlayVideo();
                }
                return true;
            }
        });
        viewBinding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VideoViewActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
            }
        });

        viewBinding.btnPlay.setOnClickListener(View -> {
            if (mNetworkState == 0) {
                if (SCHEME_HTTP.equalsIgnoreCase(mVideoUri.getScheme())) {
                    if (mPlayingPos >= mLastLoadLength - deltaTime) {
                        Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            viewBinding.videoView.start();
            viewBinding.btnPlay.setVisibility(android.view.View.INVISIBLE);
            viewBinding.btnChange.setVisibility(android.view.View.INVISIBLE);
            viewBinding.btnSeek.setVisibility(android.view.View.INVISIBLE);
        });

        viewBinding.btnSeek.setOnClickListener(view -> {
            int targetPos = 0;
            viewBinding.videoView.seekTo(targetPos);
            Toast.makeText(this, "已帮您回到开头", Toast.LENGTH_SHORT).show();
        });

        viewBinding.btnChange.setOnClickListener(View -> {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        viewBinding.backBtn.setOnClickListener(v -> finish());
    }

    /**
     * 监听网络变化,用于重新缓存
     */
    private void registerNetworkReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (SCHEME_HTTP.equalsIgnoreCase(mVideoUri.getScheme())
                            && action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        doWhenNetworkChange();
                    }
                }
            };
        }
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * 网络播放进行缓存
     */
    public void doWhenNetworkChange() {
        mNetworkState = NetworkUtils.getNetworkType(this);
        //保存当前已缓存长度
        int bufferPercentage = viewBinding.videoView.getBufferPercentage();
        mLastLoadLength = bufferPercentage * viewBinding.videoView.getDuration() / 100;
        //这里需要判断 0
        int currentPosition = viewBinding.videoView.getCurrentPosition();
        if (currentPosition > 0) {
            mPlayingPos = currentPosition;
        }

        if (mNetworkState == NetworkUtils.NETWORK_TYPE_INVALID && bufferPercentage < 100) {
            // 监听当前播放位置,在达到缓冲长度前自动停止
            if (mCheckPlayingProgressTimer == null) {
                mCheckPlayingProgressTimer = new Timer();
            }
            mCheckPlayingProgressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mPlayingPos >= mLastLoadLength - deltaTime) {
                        viewBinding.videoView.pause();
                    }
                }
            }, 0, 1000);//每秒检测一次
        } else {
            restartPlayVideo();
        }
    }

    private void unregisterNetworkReceiver() {
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
    }

    private void restartPlayVideo() {
        if (mCheckPlayingProgressTimer != null) {
            mCheckPlayingProgressTimer.cancel();
            mCheckPlayingProgressTimer = null;
        }
        viewBinding.videoView.setVideoURI(mVideoUri);
        viewBinding.videoView.start();
        viewBinding.videoView.seekTo(mPlayingPos);

        mLastLoadLength = -1;
        mPlayingPos = 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == viewBinding.videoView) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtils.getWidthInPx(this);
            float width = DensityUtils.getHeightInPx(this);
            viewBinding.videoViewContainer.getLayoutParams().height = (int) width;
            viewBinding.videoViewContainer.getLayoutParams().width = (int) height;
        } else {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtils.getWidthInPx(this);
            float height = DensityUtils.dip2px(this, 200.f);
            viewBinding.videoViewContainer.getLayoutParams().height = (int) height;
            viewBinding.videoViewContainer.getLayoutParams().width = (int) width;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNetworkState = NetworkUtils.getNetworkType(this);
        Log.d(TAG, "onResume: " + mPlayingPos);
        //播放网络视频时,需要检测判断网络状态变化
        if (SCHEME_HTTP.equalsIgnoreCase(mVideoUri.getScheme()) && mNetworkState == 0) {
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
        } else {
            if (mPlayingPos > 0) {
                viewBinding.videoView.start();
                viewBinding.videoView.seekTo(mPlayingPos);
                mPlayingPos = 0;
                mLastLoadLength = -1;
            }
        }
    }

    @Override
    protected void onPause() {
        mPlayingPos = viewBinding.videoView.getCurrentPosition();
        viewBinding.videoView.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (viewBinding.videoView.isPlaying() || viewBinding.videoView.canPause()) {
            viewBinding.videoView.stopPlayback();
        }
        mLastLoadLength = 0;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCheckPlayingProgressTimer != null) {
            mCheckPlayingProgressTimer.cancel();
            mCheckPlayingProgressTimer = null;
        }

        unregisterNetworkReceiver();
        EventUtils.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }
}