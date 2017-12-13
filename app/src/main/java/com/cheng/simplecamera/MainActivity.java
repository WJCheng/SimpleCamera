package com.cheng.simplecamera;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 1000;

    @ViewById(R.id.image)
    ImageView mIvImage;
    @ViewById(R.id.take)
    Button mBtnTake;

    @AfterViews
    void afterView() {
        initListener();
    }

    private void initListener() {
        mBtnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CameraActivity_.intent(MainActivity.this).get();
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO) {
                File imageFile = (File) data.getSerializableExtra("image");
                mIvImage.setImageURI(Uri.fromFile(imageFile));
            }
        }
    }
}
