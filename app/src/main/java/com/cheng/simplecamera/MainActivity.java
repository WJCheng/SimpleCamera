package com.cheng.simplecamera;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 1000;
    private ImageView mIvImage;
    private Button mBtnTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        initListener();
    }

    private void initLayout() {
        mIvImage = findViewById(R.id.image);
        mBtnTake = findViewById(R.id.take);
    }

    private void initListener() {
        mBtnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
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
