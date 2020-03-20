package com.example.opengldemoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnTakeImage)
    Button mBtnTakeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBtnTakeImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,EditorPhotoActivity.class);
            startActivity(intent);
        });
    }
}
