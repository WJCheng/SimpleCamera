package com.cheng.simplecamera.camera2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheng.simplecamera.R;
import com.cheng.simplecamera.base.FullScreenActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.nio.ByteBuffer;
import java.util.Arrays;

@TargetApi(21)
@EActivity(R.layout.activity_camera_new)
public class CameraNewActivity extends FullScreenActivity {

    @ViewById(R.id.text)
    TextView mText;
    @ViewById(R.id.image)
    ImageView mImage;
    @ViewById(R.id.surface)
    SurfaceView mSurfaceView;
    @ViewById(R.id.take)
    Button mTake;

    private CameraManager mCameraManager;
    private SurfaceHolder mSurfaceHolder;
    private String mCameraId;
    private ImageReader mImageReader;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private CaptureRequest.Builder mPreviewBuilder;

    @AfterViews
    void afterViews() {
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        mText.setVisibility(View.GONE);
//        try {
//            String[] cameraIdList = mCameraManager.getCameraIdList();
//            for (int i=0; i<cameraIdList.length; i++){
//                mText.append(cameraIdList[i] + "\n");
//                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraIdList[i]);
//                mText.append(cameraCharacteristics.toString() + "\n");
//                mText.append("=================================\n");
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCameraAndPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        initListener();
    }

    private void initListener() {
        mTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback, mHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initCameraAndPreview() {
        mHandlerThread = new HandlerThread("Camera2");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;
        mImageReader = ImageReader.newInstance(mSurfaceView.getWidth(), mSurfaceView.getHeight(), ImageFormat.JPEG, 7);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;}
            mCameraManager.openCamera("0", mDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            mImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            mImage.setVisibility(View.VISIBLE);

//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream());
//            Log.i(TAG, "onImageAvailable: " + reader.acquireLatestImage());
//            reader.acquireNextImage()
        }
    };

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "onOpened: ");
            mCameraDevice = camera;
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "onDisconnected: ");
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.i(TAG, "onError: error = " + error);
            camera.close();
            mCameraDevice = null;
        }
    };

    private void createCameraPreview() throws CameraAccessException {

        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewBuilder.addTarget(mSurfaceHolder.getSurface());

        mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), mPreviewStateCallback, mHandler);

//        CameraCharacteristics characteristics = null;
//        try {
//            characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        int deviceOrientation = getWindowManager().getDefaultDisplay().getOrientation();
//        int totalRotation = sensorToDeviceRotation(characteristics, deviceOrientation);
//        boolean swapRotation = totalRotation == 90 || totalRotation == 270;
//        int rotatedWidth = mWidth;
//        int rotatedHeight = mHeight;
//        if (swapRotation) {
//            rotatedWidth = mHeight;
//            rotatedHeight = mWidth;
//        }
//        mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
//        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//        Log.e("CameraActivity", "OptimalSize width: " + mPreviewSize.getWidth() + " height: " + mPreviewSize.getHeight());

    }

    private CameraCaptureSession mPreviewSession;
    private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mPreviewSession = session;
            try {
//                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                session.setRepeatingRequest(mPreviewBuilder.build(), mCaptureCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e("linc","set preview builder failed."+e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            result.getPartialResults();
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }

    };
}
