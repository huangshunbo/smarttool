package com.android.minlib.smarttool.permission;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedList;

public class PermissionFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    PermissionCallback permissionCallback;
    private boolean goSetting = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void requestPermission(@NonNull String[] permissions, PermissionCallback permissionCallback){
        goSetting = false;
        this.permissionCallback = permissionCallback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,PERMISSIONS_REQUEST_CODE);
        } else {
            permissionCallback.onPermissionsResult(new ArrayList<String>(),new ArrayList<String>(),true);
        }
    }

    public void requestPermissionWithSetting(@NonNull String[] permissions, PermissionCallback permissionCallback){
        requestPermission(permissions,permissionCallback);
        goSetting = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != PERMISSIONS_REQUEST_CODE){
            return;
        }
        LinkedList<String> refuses = new LinkedList<>();
        LinkedList<String> allows = new LinkedList<>();
        for(int i =0 ; i < permissions.length ; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                allows.add(permissions[i]);
            }else{
                refuses.add(permissions[i]);
            }
        }
        permissionCallback.onPermissionsResult(allows,refuses,refuses.size() > 0 ? false : true);
        if(goSetting && !verifyPermissions(grantResults)){
            showTipDialog();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTipDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("提示信息")
                .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                        startActivity(intent);
                    }
                }).show();
    }

    public boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
