package com.cheng.simplecamera.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Camera Basic function like take photo
 * Created by cheng on 2017/12/5.
 */
public class CameraBase extends SurfaceView implements CameraInteface {
    private static final String TAG = "CameraBase";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final int INVALID_CAMERA_ID = -1;
    public static final int MAX_VIDEO_LENGTH = 10;

    public static final int FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    protected CountDownTask countDownTask;

    protected Context mContext;
    protected Camera mCamera;
    protected Camera.Parameters mParameter;
    protected Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    protected int curCameraId = INVALID_CAMERA_ID;//init to be invalid
    protected int camFacing = FACING_BACK;
    protected int curScreenRotation;

    private int mPreviewHeight;
    private int mPreviewWidth;
    private SurfaceHolder mSurfaceHolder;
    private OrientationEventListener orientationEventListener;

    protected TextView mTvPrompt;
    protected ProgressBar mProgressBar;

    public CameraBase(Context context) {
        this(context, null);
    }

    public CameraBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mPreviewHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        mPreviewWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();

        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(surfaceCallback);
        if (Build.VERSION.SDK_INT <= 14) {
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        openCamera(FACING_BACK);//default to be facing back.

        orientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                //here orientation could be 0 ~ 360, curScreenRotation could only be 0, 90, 180, 270.
                curScreenRotation = CameraUtil.getCurrentOrientation(orientation);
            }
        };
    }

    @Override
    public void openCamera(int facing) {
        camFacing = facing;
        chooseCam();
        safelyOpenCam();
        adjustCameraParameter();
    }

    @Override
    public void chooseCam() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == camFacing) {
                curCameraId = i;
                return;
            }
        }
        curCameraId = INVALID_CAMERA_ID;
    }

    @Override
    public void safelyOpenCam() {
        try {
            mCamera = Camera.open(curCameraId);
            mCamera.setDisplayOrientation(90);
            mParameter = mCamera.getParameters();
        } catch (Exception e) {
            Log.e(TAG, "safelyOpenCam: ", e);
        }
    }

    //use for switching through back and front camera.
    public void startCamera() {
        if (mSurfaceHolder != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                Log.i(TAG, "startCamera: orientation = " + mCameraInfo.orientation);
            } catch (IOException e) {
                Log.e(TAG, "startCamera: ", e);
            }
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                Log.e(TAG, "releaseCamera: ", e);
            }
        }
    }

    @Override
    public void adjustCameraParameter() {
        mParameter.setPreviewSize(mPreviewWidth, mPreviewHeight);
        mParameter.setPictureFormat(ImageFormat.JPEG);
        mParameter.setPictureSize(mPreviewWidth, mPreviewHeight);
        if (mParameter.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mParameter.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.setParameters(mParameter);
    }

    public void switchCamera() {
        releaseCamera();
        openCamera(camFacing == FACING_BACK ? FACING_FRONT : FACING_BACK);
        startCamera();
    }

    @Override
    public void takePicture(final PhotoCallback photoCallback) {
        if (mCamera != null) {
            calcPictureRotation();//mainly rotate picture before taking picture.

            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                photoCallback.onPhotoToken(data, camera);
                                camera.autoFocus(null);//remove callback on each time the camera finish auto focus,
                                camera.startPreview();
                                setPromptVisibility(true);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "takePicture: ", e);
                    }
                }
            });
        } else {
            Log.e(TAG, "takePicture: Camera is not connect successfully.");
        }
    }

    private void calcPictureRotation() {
        if (camFacing == FACING_BACK) {
            Log.i(TAG, "calcPictureRotation: back orientation = " + mCameraInfo.orientation);//90
            if (curScreenRotation == 0) {
                mParameter.setRotation(90);
            }
            if (curScreenRotation == 90) {
                mParameter.setRotation(180);
            }
            if (curScreenRotation == 180) {
                mParameter.setRotation(270);
            }
            if (curScreenRotation == 270) {
                mParameter.setRotation(0);
            }
        } else {//Facing Front
            Log.i(TAG, "calcPictureRotation: front orientation = " + mCameraInfo.orientation);//270
            if (curScreenRotation == 0) {
                mParameter.setRotation(270);
            }
            if (curScreenRotation == 90) {
                mParameter.setRotation(180);
            }
            if (curScreenRotation == 180) {
                mParameter.setRotation(90);
            }
            if (curScreenRotation == 270) {
                mParameter.setRotation(0);
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

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated: ");
            if (mCamera == null) {
                openCamera(camFacing);
            }
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, "surfaceCreated: ", e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surfaceChanged: ");
//            if (holder.getSurface() == null) {
//                return;
//            }
//            try {
//                mCamera.stopPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                mCamera.setPreviewDisplay(holder);
//                mCamera.startPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed: ");
            releaseCamera();
        }
    };

    private boolean hasSystemFeature(String requiredFeature) {
        return mContext.getPackageManager().hasSystemFeature(requiredFeature);
    }

    //    public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//        camera.setDisplayOrientation(result);
//    }

    public Camera getmCamera() {
        return mCamera;
    }

    public int getCurrentFacing() {
        return camFacing;
    }

    public void setPromptText(TextView view){
        this.mTvPrompt = view;
    }

    public void setPromptVisibility(boolean isShow){
        if (mTvPrompt != null){
            mTvPrompt.setVisibility(isShow ? VISIBLE : INVISIBLE);
        }
    }

    public void setPromptProgressBar(ProgressBar pb){
        this.mProgressBar = pb;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        orientationEventListener.enable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        orientationEventListener.disable();
    }
}
