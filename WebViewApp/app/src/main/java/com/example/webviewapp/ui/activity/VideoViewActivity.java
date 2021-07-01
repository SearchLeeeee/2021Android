package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.databinding.ActivityVideoViewBinding;

public class VideoViewActivity extends BaseActivity {
    private static final String TAG = "VideoViewActivity";
    public ActivityVideoViewBinding viewBinding;

    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        uri = getIntent().getStringExtra("video");
        //本地视频无法播放
        uri = "file:///android_asset/video1.mp4";
        //TODO:测试用uri
//        uri = "https://media.w3.org/2010/05/sintel/trailer.mp4";
        Log.d(TAG, "onCreate: " + uri);
        viewBinding.videoView.setVideoPath(uri);
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(viewBinding.videoView);
        viewBinding.videoView.setMediaController(mediaController);
    }
}