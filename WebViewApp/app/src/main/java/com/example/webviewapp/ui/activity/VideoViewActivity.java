package com.example.webviewapp.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.databinding.ActivityVideoViewBinding;

public class VideoViewActivity extends AppCompatActivity {
    private static final String TAG = "VideoViewActivity";
    ActivityVideoViewBinding viewBinding;

    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        getSupportActionBar().hide();

        uri = getIntent().getStringExtra("video");
        Log.d(TAG, "onCreate: " + uri);
        viewBinding.videoView.setVideoURI(Uri.parse(uri));
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(viewBinding.videoView);
        viewBinding.videoView.setMediaController(mediaController);
    }
}