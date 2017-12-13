package com.cheng.simplecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cheng.simplecamera.base.FullScreenActivity;
import com.cheng.simplecamera.widget.CameraButton;
import com.cheng.simplecamera.widget.CameraInteface;
import com.cheng.simplecamera.widget.CameraUtil;
import com.cheng.simplecamera.widget.CameraView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.activity_camera)
public class CameraActivity extends FullScreenActivity {
    private static final String TAG = "CameraActivity";

    @ViewById(R.id.camera)
    CameraView mCameraView;

    @ViewById(R.id.image)
    ImageView mIvShowImage;

    @ViewById(R.id.camera_button)
    CameraButton mCameraButton;

    @ViewById(R.id.face)
    ImageView mIvSwitchFacing;

    @ViewById(R.id.close)
    ImageView mIvClose;

    @ViewById(R.id.prompt)
    TextView mTvPrompt;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.retake)
    ImageView mIvRetake;

    @ViewById(R.id.use)
    ImageView mIvUse;

    private Bitmap mBitmap;

    @AfterViews
    void afterView() {
        mIvRetake.setVisibility(View.INVISIBLE);
        mIvUse.setVisibility(View.INVISIBLE);
        initListener();
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
                    File imageFile = CameraUtil.savePhoto(CameraActivity.this, mBitmap);
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
