package com.android.minlib.smarttool.permission;

import android.support.annotation.NonNull;

import java.util.List;

public interface PermissionCallback {
    void onPermissionsResult(@NonNull List<String> allows,@NonNull List<String> refuses,boolean isAllAllow);
}
