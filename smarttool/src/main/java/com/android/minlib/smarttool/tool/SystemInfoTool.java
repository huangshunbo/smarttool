package com.android.minlib.smarttool.tool;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_2G;
import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_3G;
import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_4G;
import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_NO;
import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_UNKNOWN;
import static com.android.minlib.smarttool.tool.SmartNetworkTool.NetworkType.NETWORK_WIFI;

public class SystemInfoTool {

    //操作系统
    private static String systemVersion;
    //手机品牌
    private static String phoneBrand;
    //手机型号
    private static String phoneModel;
    //IMEI
    private static String imei;
    //App版本
    private static String appVersion;
    //UUID
    private static UUID uuid;

    public static String getSystemVersion() {
        return systemVersion = android.os.Build.VERSION.RELEASE;
    }

    public static String getPhoneBrand() {
        return phoneBrand = android.os.Build.BRAND;
    }

    public static String getPhoneModel() {
        return phoneModel = android.os.Build.MODEL;
    }

    public static String getImei(Context context) {
        String[] imeis = getIMEIS(context);
        if(imeis == null || imeis.length <= 0){
            return "-1";
        }
        if(imeis.length>=2 && !TextUtils.isEmpty(imeis[1])){
            imei = "IMEI1:" + imeis[0] + "\nIMEI2:" + imeis[1];
        }else {
            imei = "IMEI1:" + imeis[0];
        }
        return imei;
    }
    private static String[] getIMEIS(Context context) {
        String[] imeis = new String[2];
        imeis[0] = getOperatorBySlot( "getDeviceId", 0,context);
        imeis[1] = getOperatorBySlot( "getDeviceId", 1,context);
        return imeis;
    }
    /**
     * <br> Description: 反射获取手机服务信息
     * <br> Author:      zhongweijie
     * <br> Date:        2017/10/27 15:44
     */
    private static String getOperatorBySlot(String predictedMethodName, int slotID,Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        String inumeric = "";
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object obPhone = getSimID.invoke(telephony, obParameter);
            if (obPhone != null) {
                inumeric = obPhone.toString();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return inumeric;
    }

    public static String getAppVersion(Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        if (TextUtils.isEmpty(appVersion)) {
            try {
                appVersion = context.getPackageManager().
                        getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appVersion;
    }

    public static UUID getUUID(Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        String PREFS_FILE = "device_id.xml";
        String PREFS_DEVICE_ID = "device_id";
        if (uuid == null) {
            synchronized (SystemInfoTool.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context
                            .getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Settings.Secure.getString(
                                context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                String deviceId = null;
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    deviceId = null;
                                }else{
                                    deviceId = ((TelephonyManager)
                                            context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                }
                                uuid = deviceId != null ? UUID
                                        .nameUUIDFromBytes(deviceId
                                                .getBytes("utf8")) : UUID
                                        .randomUUID();

                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit()
                                .putString(PREFS_DEVICE_ID, uuid.toString())
                                .commit();
                    }
                }
            }
        }
        return uuid;
    }

    public static StringBuilder string(Context context) {
        StringBuilder buf = new StringBuilder();
        buf.append("操作系统:" + getSystemVersion() + "\n");
        buf.append("手机品牌:" + getPhoneBrand() + "\n");
        buf.append("手机型号:" + getPhoneModel() + "\n");
        buf.append("手机唯一标识码:" + getImei(context) + "\n");
        buf.append("App版本:" + getAppVersion(context) + "\n");
        buf.append("UUID " + getUUID(context) + "\n");
        return buf;
    }

    /**
     * 描述: 打开App
     *
     * @param packageName 包名
     */
    public static void startApp(String packageName,Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        if (isSpace(packageName)) return;
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
    }
    /**
     * 是否安装了指定包名的App
     * @param packageName App包名
     * @return
     */
    public static boolean isInstallApp(String packageName,Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo info = pkgList.get(i);
            if (info.packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    /**
     * 描述：打开并安装文件.
     *
     * @param file apk文件路径
     */
    public static void installApk(File file,Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取PackageInfo
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 获取应用签名
     *
     * @param pkgName 包名
     * @return 返回应用的签名
     */
    public static String getSign(String pkgName,Context context) {
        if(context == null){
            throw new IllegalArgumentException("please call init first");
        }
        try {
            PackageInfo pis = context.getPackageManager()
                    .getPackageInfo(pkgName,
                            PackageManager.GET_SIGNATURES);
            return hexDigest(pis.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 将签名字符串转换成需要的32位签名
     *
     * @param paramArrayOfByte 签名byte数组
     * @return 32位签名字符串
     */
    private static String hexDigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97,
                98, 99, 100, 101, 102 };
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
