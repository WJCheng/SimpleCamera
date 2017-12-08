package com.cheng.simplecamera.widget;

import android.hardware.Camera;

/**
 *
 * Created by cheng on 2017/12/5.
 */

public interface CameraInteface {

    void openCamera(int facing);
    void chooseCam();
    void safelyOpenCam();
    void adjustCameraParameter();
    void takePicture(PhotoCallback photoCallback);

    interface PhotoCallback {
        void onPhotoToken(byte[] data, Camera camera);
    }
}
