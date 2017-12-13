package com.cheng.simplecamera.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 *
 * Created by cheng on 2017/12/13.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity{
    public String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
