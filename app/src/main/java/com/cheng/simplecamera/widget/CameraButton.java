package com.cheng.simplecamera.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cheng.simplecamera.R;

/**
 *
 * Created by cheng on 2017/12/7.
 */

public class CameraButton extends android.support.v7.widget.AppCompatButton {
    private static final String TAG = "CameraButton";

    private Context mContext;
    private CameraView mCameraView;
    private CameraInteface.PhotoCallback mPhotoCallback;
    private boolean isRecordingVideo;

    public CameraButton(Context context) {
        this(context, null);
    }

    public CameraButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.btn_shoot);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoCallback != null){
                    mCameraView.takePicture(mPhotoCallback);
                }
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG, "onLongClick: ");
                isRecordingVideo = true;
                mCameraView.startRecordVideo();
                return false;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRecordingVideo && event.getAction() == MotionEvent.ACTION_UP){
                    mCameraView.stopRecordVideo();
                    isRecordingVideo = false;
                }
                return false;//do not consume the event.
            }
        });
    }

    public void bindCameraView(CameraView view){
        if (view != null){
            mCameraView = view;
        }
    }

    public void setPhotoCallback(CameraInteface.PhotoCallback callback){
        mPhotoCallback = callback;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onTouchEvent: " + event);
//        if (isRecordingVideo && event.getAction() == MotionEvent.ACTION_UP){
//            mCameraView.stopRecordVideo();
//            isRecordingVideo = false;
//            Toast.makeText(mContext, "stop and saving video file.", Toast.LENGTH_SHORT).show();
//        }
//        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            performClick();
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean performClick() {
//        return super.performClick();
//    }

}
