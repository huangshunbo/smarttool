package com.android.minlib.samplesimplewidget;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.minlib.smarttool.permission.PermissionCallback;
import com.android.minlib.smarttool.permission.SmartPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public static Application application;

    ListView mListView;
    private static final String[] strs =
            {
                    "verifyStoragePermissions","PickerView + PhotoTool"
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = getApplication();
        mListView = new ListView(this);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs));
        mListView.setOnItemClickListener(this);
        setContentView(mListView);
    }
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {

        SmartPermission.getInstance()
                .create(activity)
                .addPermission(PERMISSIONS_STORAGE)
                .requestPermission(new PermissionCallback() {
                    @Override
                    public void onPermissionsResult(@NonNull List<String> allows, @NonNull List<String> refuses, boolean isAllAllow) {
                        if(isAllAllow){
                            Log.d("hsb","AllAllow");
                        }
                        if(allows.size() > 0){
                            Log.d("hsb","Allows : " + allows.toString());
                        }
                        if(refuses.size() > 0) {
                            Log.d("hsb", "Refuses : " + refuses.toString());
                        }

                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                verifyStoragePermissions(this);
                break;
            case 1:
                startActivity(new Intent(this,PhotoToolActivity.class));
                break;

        }
    }

}
