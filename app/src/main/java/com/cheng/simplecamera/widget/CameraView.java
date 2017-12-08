package com.cheng.simplecamera.widget;

import android.content.Context;
import android.content.Intent;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.cheng.simplecamera.R;
import com.cheng.simplecamera.VideoPlayActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        setPromptVisibility(false);
        countDownTask = new CountDownTask(mProgressBar);
        countDownTask.execute(MAX_VIDEO_LENGTH);

        recorder = new MediaRecorder();
        if (!prepareMediaRecorder())
            return;
        recorder.start();
    }

    private boolean prepareMediaRecorder() {
        mCamera.unlock();
        recorder.setCamera(getmCamera());
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        recorder.setMaxDuration(10 * 1000);
        recorder.setOrientationHint(camFacing == FACING_BACK ? 90 : 270);
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
        setPromptVisibility(true);
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
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
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
        videoFile = getOutputMediaFile(mediaType);
        curVideoFilePath = videoFile == null ? "" : videoFile.toString();
        return curVideoFilePath;
    }

    private File getOutputMediaFile(int mediaType) {

        File mediaStoreDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getResources().getString(R.string.app_name));

        if (!mediaStoreDir.exists()) {
            if (!mediaStoreDir.mkdir()) {
                Log.e(TAG, "getOutputMediaFile: make dir failed! ");
                return null;
            }
        }

        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        if (mediaType == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (mediaType == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
