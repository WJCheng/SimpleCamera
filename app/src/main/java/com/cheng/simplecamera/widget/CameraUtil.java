package com.cheng.simplecamera.widget;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.SparseIntArray;

/**
 *
 * Created by cheng on 2017/12/6.
 */

public class CameraUtil {

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
}
