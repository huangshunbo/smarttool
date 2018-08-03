package com.android.minlib.smarttool.tool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.minlib.smarttool.tool.assist.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarTool {

    private  StatusBarTool() {
    }
    /**
     *<br> Description: 隐藏Title，需要在setContent之前调用
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/24 16:02
     */
    public static void noTitle(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(activity instanceof AppCompatActivity){
            ((AppCompatActivity)activity).supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    /**
     *<br> Description: 全屏
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/24 16:03
     */
    public static void FLAG_FULLSCREEN(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    /**
     * 判断状态栏是否存在
     *
     * @param activity activity
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isStatusBarExists(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        return (params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
    /**
     * 获取ActionBar高度
     *
     * @param activity activity
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }
    /**
     *<br> Description: 设置StatusBar颜色
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/24 16:01
     */
    public static void setStatusBarColor(Activity activity, String color, boolean dark) {
        setStatusBarColor(activity, Color.parseColor(color), dark);
    }

    public static void setStatusBarColor(Activity activity, int color, boolean dark) {
        if (Build.VERSION.SDK_INT >= 21) {
            setStatusBarColorLOLLIPOP(activity, color);
        } else if (Build.VERSION.SDK_INT >= 19) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);
            tintManager.setStatusBarTintColor(color);
        }

        StatusBarDarkMode(activity, dark, color);
    }

    @TargetApi(21)
    private static void setStatusBarColorLOLLIPOP(Activity activity, int colorID) {
        Window window = activity.getWindow();
        window.clearFlags(201326592);
        int option = 3328;
        window.getDecorView().setSystemUiVisibility(option);
        window.addFlags(-2147483648);
        window.setStatusBarColor(colorID);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        int bits = 67108864;
        if (on) {
            winParams.flags |= 67108864;
        } else {
            winParams.flags &= -67108865;
        }

        win.setAttributes(winParams);
    }

    private static int StatusBarDarkMode(Activity activity, boolean dark, int color) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            if (MIUISetStatusBarDarkMode(activity.getWindow(), dark)) {
                result = 1;
            }

            if (FlymeSetStatusBarDarkMode(activity.getWindow(), dark)) {
                result = 2;
            }

            if (Build.VERSION.SDK_INT < 23) {
                if (result == 0 && (color == -1 || color == Color.parseColor("#F5F5F7"))) {
                    setStatusBarColor(activity, -16777216, false);
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                if (dark) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(9984);
                }

                result = 3;
            }
        }

        return result;
    }

    private static boolean FlymeSetStatusBarDarkMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object)null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception var8) {
                ;
            }
        }

        return result;
    }

    private static boolean MIUISetStatusBarDarkMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();

            try {
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", Integer.TYPE, Integer.TYPE);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }

                result = true;
            } catch (Exception var8) {
                return false;
            }
        }

        return result;
    }

    private static void invokePanels(Context context, String methodName) {
        try {
            Object service = context.getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName);
            expand.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *<br> Description: 设置状态栏字体图标为深色，需要MIUIV6以上
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/24 16:07
     */
    private static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     *<br> Description: 设置状态栏图标为深色和魅族特定的文字风格
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/24 16:07
     */
    private static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
}
