package com.cheng.simplecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.cheng.simplecamera.base.FullScreenActivity;
import com.cheng.simplecamera.widget.CameraUtil;
import com.iceteck.silicompressorr.SiliCompressor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.net.URISyntaxException;

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

    private String mRawPath;
    private String compressedPath = "";

    @AfterViews
    void initLayout() {
        mRawPath = getIntent().getStringExtra("path");

        initListener();
        initVideoPlayer();
//        getThumbnail();
        byte[] bytes = CameraUtil.getThumbnailBytes(mRawPath);
        mThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

    }

    private void getThumbnail() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mRawPath);

        Bitmap bitmap = retriever.getFrameAtTime();
        if (bitmap != null) {
            mThumbnail.setImageBitmap(bitmap);
        }
    }

    private void initVideoPlayer() {
        mVideoView.setVideoPath(mRawPath);
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
                VideoPlayActivity.this.showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //store the compressed video and the raw video in the same directory.
                        String compressedDir = new File(mRawPath).getParent();
                        try {
                            compressedPath = SiliCompressor.with(VideoPlayActivity.this)
                                    .compressVideo(mRawPath, compressedDir);
                        } catch (URISyntaxException e) {
                            Log.e(TAG, "run: ", e);
                        }
                        Log.i(TAG, "initLayout: path = " + compressedPath);
                        Log.i(TAG, "initLayout: file = " + new File(compressedPath).exists());
                        setResult(RESULT_OK, new Intent().putExtra("compressedPath", compressedPath));
                        finish();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                VideoPlayActivity.this.hideProgressDialog();
                            }
                        });
                    }
                }).start();
            }
        });
    }

}
