package com.android.minlib.samplesimplewidget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.android.minlib.smarttool.tool.PhotoTool;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static com.bigkoo.pickerview.configure.PickerOptions.TYPE_PICKER_OPTIONS;

public class PhotoToolActivity extends AppCompatActivity{

    OptionsPickerView pickerView;
    ImageView imageView;

    String[] strs = new String[]{
            "get photo from Camera","get photo from Album","get photo from Pictrues"
    };
    File tmpFile ;
    File dstFile;

    private static final int CAMERA_CODE = 1;
    private static final int ALBUM_CODE = 2;
    private static final int PICTURE_CODE = 3;
    private static final int COMPRESS_CODE = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phototool);
        imageView = findViewById(R.id.imageview);
        tmpFile = new File(getExternalCacheDir().getAbsolutePath() + "/tmp.jpeg");
        dstFile = new File(getExternalCacheDir().getAbsolutePath() + "/dst.jpeg");
        if(!tmpFile.exists()){
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final PickerOptions options = new PickerOptions(TYPE_PICKER_OPTIONS);
        options.context = this;
        options.isDialog = true;
        pickerView = new  OptionsPickerView(options);
        pickerView.setPicker(Arrays.asList(strs));
        pickerView.show();


        options.optionsSelectListener = new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if(options1 == 0){
                    openCamera();
                }else if(options1 == 1){
                    openAlbum();
                }else if(options1 == 2){
                    openPictures();
                }
            }
        };

    }

    private void openPictures(){
        PhotoTool.openAlbumFromSystem(this,PICTURE_CODE);
    }

    private void openAlbum() {
        PhotoTool.openAlbumFromThrid(this,ALBUM_CODE);
    }

    private void openCamera() {
        Uri uri = FileProvider.getUriForFile(this,"com.android.minlib.samplesimplewidget",tmpFile);
        PhotoTool.openCamera(PhotoToolActivity.this,uri ,CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_CODE){
                Uri org = FileProvider.getUriForFile(this,"com.android.minlib.samplesimplewidget",tmpFile);
                Uri dst = Uri.fromFile(dstFile);
                PhotoTool.cropImageUri(this,org,dst,COMPRESS_CODE);
            }else if(requestCode == ALBUM_CODE){
                Uri org = data.getData();
                Uri dst = Uri.fromFile(dstFile);
                PhotoTool.cropImageUri(this,org,dst,COMPRESS_CODE);
            }else if(requestCode == PICTURE_CODE){
                Uri org = data.getData();
                Uri dst = Uri.fromFile(dstFile);
                PhotoTool.cropImageUri(this,org,dst,COMPRESS_CODE);
            }else if(requestCode == COMPRESS_CODE){
                Bitmap bitmap = BitmapFactory.decodeFile(dstFile.getAbsolutePath());
//                imageView.setImageBitmap(bitmap);
                PhotoTool.compressImageView(imageView,dstFile.getAbsolutePath());
            }
        }
    }
}
