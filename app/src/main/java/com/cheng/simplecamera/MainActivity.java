package com.cheng.simplecamera;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.cheng.simplecamera.base.BaseActivity;
import com.cheng.simplecamera.camera2.Camera2Activity_;
import com.cheng.simplecamera.common.ConstUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewById(R.id.image)
    ImageView mIvImage;
    @ViewById(R.id.video)
    VideoView mVVideo;
    @ViewById(R.id.take)
    Button mBtnTake;

    @AfterViews
    void afterView() {
        initListener();
    }

    private void initListener() {
        mBtnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = CameraActivity_.intent(MainActivity.this).get();
                Intent intent = Camera2Activity_.intent(MainActivity.this).get();
                startActivityForResult(intent, ConstUtil.REQUEST_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstUtil.RESULT_CAM_IMAGE) {
            File imageFile = (File) data.getSerializableExtra("image");
            if (imageFile != null && imageFile.exists()) {
                mIvImage.setImageURI(Uri.fromFile(imageFile));
                mIvImage.setVisibility(View.VISIBLE);
                mVVideo.setVisibility(View.INVISIBLE);
            }
        }
        if (resultCode == ConstUtil.RESULT_CAM_VIDEO) {
            String videoPath = data.getStringExtra("compressedPath");
            if (videoPath != null) {
                mVVideo.setVideoPath(videoPath);
                mVVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        mp.start();
                    }
                });
                mVVideo.setVisibility(View.VISIBLE);
                mIvImage.setVisibility(View.INVISIBLE);
            }
        }
    }

}
