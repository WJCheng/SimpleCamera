package com.cheng.simplecamera;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private Button mBtnPlay;
    private ImageView mIvUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        initLayout();
        initListener();

        initVideoPlayer();
    }

    private void initVideoPlayer() {
        String path = getIntent().getStringExtra("path");
        mVideoView.setVideoPath(path);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    private void initListener() {
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.start();
            }
        });
        mIvUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoPlayActivity.this, "Use it for what!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fullScreen() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initLayout() {
        fullScreen();
        mVideoView = findViewById(R.id.video_view);
        mBtnPlay = findViewById(R.id.play);
        mIvUse = findViewById(R.id.use);
    }
}
