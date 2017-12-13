package com.cheng.simplecamera.widget;

import android.content.Context;
import android.content.Intent;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.cheng.simplecamera.VideoPlayActivity_;

import java.io.File;
import java.io.IOException;

/**
 * CameraView extend from CameraBase which can record video.
 * Created by cheng on 2017/12/7.
 */
public class CameraView extends CameraBase {
    private static final String TAG = "CameraView";

    private MediaRecorder recorder;
    private String curVideoFilePath;
    private File videoFile;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startRecordVideo() {
        countDownTask = new CountDownTask(mProgressBar);
        countDownTask.execute(MAX_VIDEO_LENGTH_SECOND);

        recorder = new MediaRecorder();
        calcVideoRotation();
        if (!prepareMediaRecorder())
            return;
        recorder.start();
    }

    protected void calcVideoRotation() {
        if (camFacing == FACING_BACK) {
            Log.i(TAG, "calcPictureRotation: back orientation = " + mCameraInfo.orientation);//90
            if (curScreenRotation == 0) {
                recorder.setOrientationHint(90);
            }
            if (curScreenRotation == 90) {
                recorder.setOrientationHint(180);
            }
            if (curScreenRotation == 180) {
                recorder.setOrientationHint(270);
            }
            if (curScreenRotation == 270) {
                recorder.setOrientationHint(0);
            }
        } else {//Facing Front
            Log.i(TAG, "calcPictureRotation: front orientation = " + mCameraInfo.orientation);//270
            if (curScreenRotation == 0) {
                recorder.setOrientationHint(270);
            }
            if (curScreenRotation == 90) {
                recorder.setOrientationHint(180);
            }
            if (curScreenRotation == 180) {
                recorder.setOrientationHint(90);
            }
            if (curScreenRotation == 270) {
                recorder.setOrientationHint(0);
            }
        }
        mCamera.setParameters(mParameter);
        if (Build.VERSION.SDK_INT <= 14) {
            try {
                mCamera.stopPreview();
                mCamera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "takePicture: sdk < 14 need to process: stop and start preview.", e);
            }
        }
    }

    private boolean prepareMediaRecorder() {
        mCamera.unlock();
        recorder.setCamera(getmCamera());
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        recorder.setMaxDuration(10 * 1000);
        recorder.setMaxFileSize(1024 * 1024 * 5);
//        recorder.setOrientationHint(camFacing == FACING_BACK ? 90 : 270);
        recorder.setOutputFile(getOutputMediaFilePath(MEDIA_TYPE_VIDEO));
        recorder.setPreviewDisplay(this.getHolder().getSurface());

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepareMediaRecorder: IOException ", e);
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * if stop is called after start in a very short time, probably 1 second，a RuntimeException may throw.
     * according to official doc and StackOverflow, we need to handle that this way: delete the file.
     *
     * reason: stop too fast after start, and the MediaRecorder haven't receive data yet.
     */
    public void stopRecordVideo() {
        countDownTask.cancel(true);
        mProgressBar.setVisibility(INVISIBLE);

        try {
            recorder.stop();
        } catch (RuntimeException e) {
            Log.e(TAG, "stopRecordVideo: maybe stop too fast after start, and recorder receive no data.", e);
            Toast.makeText(mContext, "too short! try again. ", Toast.LENGTH_SHORT).show();
            videoFile.delete();
        } finally {
            releaseMediaRecorder();
        }

        if (videoFile.exists()) {//record video successfully.
            Intent intent = VideoPlayActivity_.intent(mContext).get();
            intent.putExtra("path", curVideoFilePath);
            mContext.startActivity(intent);
        }
    }

    public void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
            mCamera.lock();
        }
    }

    private String getOutputMediaFilePath(int mediaType) {
        videoFile = CameraUtil.getOutputMediaFile(mContext, mediaType);
        curVideoFilePath = videoFile == null ? "" : videoFile.toString();
        return curVideoFilePath;
    }

}
