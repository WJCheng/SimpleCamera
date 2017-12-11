package com.cheng.simplecamera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.util.SparseIntArray;

import com.cheng.simplecamera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * Created by cheng on 2017/12/6.
 */

public class CameraUtil {
    private static final String TAG = "CameraUtil";

    private static SparseIntArray array = new SparseIntArray();
    private static int[] degrees = new int[]{0, 90, 180, 270};

    /**
     * key 0, 270, 180, 90 means bottom, left, top, right edge of the phone
     * value means the degree that picture taken by the camera should rotate.
     * camera picture default right edge as the bottom. if want to fit the bottom of the phone, need to rotate 90 degree
     */
//    static {
//        array.append(0, 90);
//        array.append(90, 0);
//        array.append(180, 270);
//        array.append(270, 180);
//    }

    static int getCurrentOrientation(int orientation){
        int currentOrientation = 0;
        if (orientation >= 315){
            currentOrientation = 0;
        } else {
            for (int degree : degrees) {
                if (Math.abs(orientation - degree) <= 45) {
                    currentOrientation = degree;
                }
            }
        }
        return currentOrientation;
    }

//    public static int getPictureOrientation(int orientation){
//        int currentOrientation = 0;
//        if (orientation >= 315){
//            currentOrientation = 0;
//        } else {
//            for (int degree : degrees) {
//                if (Math.abs(orientation - degree) <= 45) {
//                    currentOrientation = degree;
//                }
//            }
//        }
//        return array.get(currentOrientation);
//    }

    /**
     * usage: Mirror image flipping for Front Camera.
     * @param bmp raw bitmap
     * @return bitmap after flipping
     */
    static Bitmap convertBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转

        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

    static File getOutputMediaFile(Context context, int mediaType) {

        File mediaStoreDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getResources().getString(R.string.app_name));

        if (!mediaStoreDir.exists()) {
            if (!mediaStoreDir.mkdir()) {
                Log.e(TAG, "getOutputMediaFile: make dir failed! ");
                return null;
            }
        }

        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        if (mediaType == CameraView.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (mediaType == CameraView.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static void savePicture(Context context, Bitmap bitmap) {
        try {
            FileOutputStream fos = context.openFileOutput("new_pic_" + System.currentTimeMillis() + ".jpg", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
