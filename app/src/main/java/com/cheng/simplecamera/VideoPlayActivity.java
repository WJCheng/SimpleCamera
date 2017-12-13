package com.cheng.simplecamera;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cheng.simplecamera.base.FullScreenActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_video_play)
public class VideoPlayActivity extends FullScreenActivity {

    @ViewById(R.id.video_view)
    VideoView mVideoView;
    @ViewById(R.id.play)
    Button mBtnPlay;
    @ViewById(R.id.use)
    ImageView mIvUse;
    @ViewById(R.id.thumbnail)
    ImageView mThumbnail;

    private String mPath;

    @AfterViews
    void initLayout() {
        mPath = getIntent().getStringExtra("path");

        initListener();
        initVideoPlayer();
        getThumbnail();
    }

    private void getThumbnail() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mPath);

        Bitmap bitmap = retriever.getFrameAtTime();
        if (bitmap != null){
            mThumbnail.setImageBitmap(bitmap);
        }
    }

    private void initVideoPlayer() {
        mVideoView.setVideoPath(mPath);
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
                Log.i(TAG, "onClick: ");
                Toast.makeText(VideoPlayActivity.this, "Use it for what!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
