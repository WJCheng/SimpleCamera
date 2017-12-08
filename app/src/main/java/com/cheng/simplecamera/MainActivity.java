package com.cheng.simplecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cheng.simplecamera.widget.CameraButton;
import com.cheng.simplecamera.widget.CameraInteface;
import com.cheng.simplecamera.widget.CameraView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CameraView mCameraView;
    private ImageView mIvShowImage;
    private CameraButton mCameraButton;
    private ImageView mIvSwitchFacing;
    private TextView mTvPrompt;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        initListener();
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
        mCameraView = findViewById(R.id.camera);
        mIvShowImage = findViewById(R.id.image);
        mIvSwitchFacing = findViewById(R.id.face);
        mCameraButton = findViewById(R.id.camera_button);
        mTvPrompt = findViewById(R.id.prompt);
        mProgressBar = findViewById(R.id.progressBar);
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

        mCameraButton.bindCameraView(mCameraView);
        mCameraView.setPromptText(mTvPrompt);
        mCameraView.setPromptProgressBar(mProgressBar);
        mCameraButton.setPhotoCallback(new CameraInteface.PhotoCallback() {
            @Override
            public void onPhotoToken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,  0, data.length);
                mIvShowImage.setImageBitmap(bitmap);
                savePicture(bitmap);
            }
        });
    }

    private void savePicture(Bitmap bitmap) {
        try {
            FileOutputStream fos = openFileOutput("new_pic_" + System.currentTimeMillis() + ".jpg", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
