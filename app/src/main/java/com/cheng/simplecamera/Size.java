package com.cheng.simplecamera;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by cheng on 2017/12/14.
 */

public class Size implements Parcelable{

    protected Size(Parcel in) {

    }

    public static final Creator<Size> CREATOR = new Creator<Size>() {
        @Override
        public Size createFromParcel(Parcel in) {
            return new Size(in);
        }

        @Override
        public Size[] newArray(int size) {
            return new Size[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
