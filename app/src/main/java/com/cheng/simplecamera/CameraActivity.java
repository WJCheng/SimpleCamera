package com.cheng.simplecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cheng.simplecamera.base.FullScreenActivity;
import com.cheng.simplecamera.widget.CameraButton;
import com.cheng.simplecamera.widget.CameraInteface;
import com.cheng.simplecamera.widget.CameraUtil;
import com.cheng.simplecamera.widget.CameraView;

import java.io.File;

public class CameraActivity extends FullScreenActivity {
    private static final String TAG = "CameraActivity";

    private CameraView mCameraView;
    private ImageView mIvShowImage;
    private CameraButton mCameraButton;
    private ImageView mIvSwitchFacing;
    private ImageView mIvClose;
    private TextView mTvPrompt;
    private ProgressBar mProgressBar;
    private ImageView mIvRetake;
    private ImageView mIvUse;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initLayout();
        initListener();
    }

    private void initLayout() {
        mCameraView = findViewById(R.id.camera);
        mIvShowImage = findViewById(R.id.image);
        mIvSwitchFacing = findViewById(R.id.face);
        mIvClose = findViewById(R.id.close);
        mCameraButton = findViewById(R.id.camera_button);
        mTvPrompt = findViewById(R.id.prompt);
        mProgressBar = findViewById(R.id.progressBar);
        mIvRetake = findViewById(R.id.retake);
        mIvUse = findViewById(R.id.use);

        mIvRetake.setVisibility(View.INVISIBLE);
        mIvUse.setVisibility(View.INVISIBLE);
    }

    private void initListener() {
        mIvSwitchFacing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    mCameraView.switchCamera();
                }
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    File imageFile = CameraUtil.savePicture(CameraActivity.this, mBitmap);
                    setResult(RESULT_OK, new Intent().putExtra("image", imageFile));
                    CameraActivity.this.finish();
                }
            }
        });
        mIvRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;

                    mCameraView.startPreview();
                    mIvShowImage.setImageBitmap(null);
                    mIvShowImage.setVisibility(View.INVISIBLE);
                    showUseController(false);
                }
            }
        });

        mCameraButton.bindCameraView(mCameraView);
        mCameraView.setPromptProgressBar(mProgressBar);
        mCameraButton.setPhotoCallback(new CameraInteface.PhotoCallback() {
            @Override
            public void onPhotoToken(byte[] data, Camera camera) {
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                mIvShowImage.setImageBitmap(mBitmap);
                showUseController(true);
            }
        });
    }

    private void showUseController(boolean isShow) {
        int useVisibility = isShow ? View.VISIBLE : View.INVISIBLE;//ready to use the taken photo
        int takeVisibility = isShow ? View.INVISIBLE : View.VISIBLE;//ready to take photo

        mIvRetake.setVisibility(useVisibility);
        mIvUse.setVisibility(useVisibility);
        mIvShowImage.setVisibility(useVisibility);

        mTvPrompt.setVisibility(takeVisibility);
        mCameraButton.setVisibility(takeVisibility);
        mIvClose.setVisibility(takeVisibility);
        mIvSwitchFacing.setVisibility(takeVisibility);
    }

}
