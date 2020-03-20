package com.example.opengldemoapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditorPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1234;
    @BindView(R.id.gpuimage)
    GPUImageView mGpuImageView;
    @BindView(R.id.btnChooseFilter)
    Button mBtnChooseFilter;
    @BindView(R.id.seekBarAdjust)
    SeekBar mSeekBarAdjust;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private GPUImageFilterTools.FilterAdjuster adjuster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_photo);
        ButterKnife.bind(this);
        initToolBar();
        startPhotoPicker();
        mSeekBarAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjuster.adjust(progress);
                mGpuImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBtnChooseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPUImageFilterTools.showDialog(EditorPhotoActivity.this, new Function1<GPUImageFilter, Unit>() {
                    @Override
                    public Unit invoke(GPUImageFilter filter) {
                        switchFilterTo(filter);
                        mGpuImageView.requestRender();
                        return null;
                    }
                });
            }
        });
    }
    private void initToolBar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void startPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                mGpuImageView.setImage(data.getData());
            }
        }
    }
    private void switchFilterTo(GPUImageFilter filter) {
        if (mGpuImageView.getFilter() == null|| mGpuImageView.getFilter() != filter){
            mGpuImageView.setFilter(filter);
            adjuster = new GPUImageFilterTools.FilterAdjuster(filter);
            if (adjuster.canAdjust()){
                mSeekBarAdjust.setVisibility(View.VISIBLE);
                adjuster.adjust(mSeekBarAdjust.getProgress());

            }else {
                mSeekBarAdjust.setVisibility(View.GONE);
            }
        }
    }
}
