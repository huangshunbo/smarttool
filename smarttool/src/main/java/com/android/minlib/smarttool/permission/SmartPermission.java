package com.android.minlib.smarttool.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;

public class SmartPermission {

    private static final String TAG = "PermissionFragment";
    private PermissionFragment permissionFragment;
    private String[] permissions = new String[0];

    private static SmartPermission smartPermission = null;

    private SmartPermission() {
    }

    public static SmartPermission getInstance() {
        if (smartPermission == null) {
            synchronized (SmartPermission.class) {
                if (smartPermission == null) {
                    smartPermission = new SmartPermission();
                }
            }
        }
        return smartPermission;
    }

    public SmartPermission create(Activity activity) {
        permissionFragment = getPermissionsFragment(activity);
        return this;
    }

    public SmartPermission addPermission(String ...permissions) {
        this.permissions = permissions;
        return this;
    }

    public void requestPermission(PermissionCallback permissionCallback) {
        check();
        permissionFragment.requestPermission(this.permissions, permissionCallback);
    }

    public void requestPermissionWithSetting(PermissionCallback permissionCallback){
        check();
        permissionFragment.requestPermissionWithSetting(this.permissions, permissionCallback);
    }

    private void check(){
        if (permissionFragment == null) {
            throw new IllegalArgumentException("请先调用create方法");
        }
        if (this.permissions == null || this.permissions.length <= 0) {
            throw new IllegalArgumentException("请先调用addPermission添加需要请求的权限");
        }
    }

    private PermissionFragment getPermissionsFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        PermissionFragment permissionFragment = (PermissionFragment) fragmentManager.findFragmentByTag(TAG);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionFragment;
    }

    public static boolean hasPermission(Context context, String permission){
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

}
