package com.android.minlib.samplesimplewidget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.android.minlib.smarttool.permission.PermissionCallback;
import com.android.minlib.smarttool.permission.SmartPermission;
import com.android.minlib.smarttool.tool.ScreenTool;
import com.android.minlib.smarttool.tool.SmartDateTool;
import com.android.minlib.smarttool.tool.SmartNetworkTool;
import com.android.minlib.smarttool.tool.StatusBarTool;
import com.android.minlib.smarttool.tool.SystemInfoTool;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.List;

import static com.bigkoo.pickerview.configure.PickerOptions.TYPE_PICKER_OPTIONS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    private static final String[] strs ={
            "verifyStoragePermissions","PickerView + PhotoTool","StatusBarTool","ScreenTool",
            "SmartDateTool","SmartNetworkTool","SystemInfoTool"
    };

    ListView mListView;
    TextView tvText;
    ImageView ivImage;
    PickerOptions options = new PickerOptions(TYPE_PICKER_OPTIONS);
    OptionsPickerView pickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarTool.noTitle(this);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.activity_main_listview);
        tvText = findViewById(R.id.activity_main_txt);
        ivImage = findViewById(R.id.activity_main_image);

        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs));
        mListView.setOnItemClickListener(this);

        options.context = this;
        options.isDialog = true;
        pickerView = new OptionsPickerView(options);

    }
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public void verifyStoragePermissions(Activity activity) {

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

    private void setStatusBar(int color){
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1,-1);
        lp.topMargin = StatusBarTool.getStatusBarHeight(this);//获取状态栏的高度
        mListView.setLayoutParams(lp);//设置完颜色会入侵状态栏，所以需要把状态栏的高度margin抵消掉
        StatusBarTool.setStatusBarColor(this,color,false);//设置状态栏颜色
        StatusBarTool.StatusBarLightMode(this);//改变状态栏字体颜色
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goneView();
        switch(position){
            case 0:
                verifyStoragePermissions(this);
                break;
            case 1:
                startActivity(new Intent(this,PhotoToolActivity.class));
                break;
            case 2:
                setStatusBar(R.color.color_orange);
                break;
            case 3:
                startScreenTool();
                break;
            case 4:
                startDateTool();
                break;
            case 5:
                startNetwork();
                break;
            case 6:
                startSystemInfo();
                break;
        }
    }

    private void startSystemInfo() {
        SystemInfoTool.getSystemVersion();
        SystemInfoTool.getPhoneBrand();
        SystemInfoTool.getPhoneModel();
        SystemInfoTool.getImei(this);
        SystemInfoTool.getAppVersion(this);
        SystemInfoTool.getUUID(this);
        tvText.setVisibility(View.VISIBLE);
        tvText.setText("系统信息 \n" + SystemInfoTool.string(this) + "\n"
            +"包名" + SystemInfoTool.getPackageInfo(this).packageName + "\n"
            +"签名" + SystemInfoTool.getSign(SystemInfoTool.getPackageInfo(this).packageName,this)
        );
    }

    private void startNetwork() {
        tvText.setVisibility(View.VISIBLE);
        boolean wifiEnable = SmartNetworkTool.isWifiAvailable(this);
        tvText.setText("WifiEnable : " + wifiEnable +"\n"
            +"ping ok ? " + SmartNetworkTool.ping() +"\n"
            + "is4G : " +SmartNetworkTool.is4G(this) +"\n"
            +"运营商名 :" + SmartNetworkTool.getNetworkOperatorName(this) +"\n"
            +"网络类型 :" + SmartNetworkTool.getNetworkType(this) +"\n"
            +"IP :" + SmartNetworkTool.getIPAddress(true) +"\n"
        );
        SmartNetworkTool.setWifiEnabled(!wifiEnable,this);
        SmartNetworkTool.openWirelessSettings(this);
    }

    private void startDateTool() {
        tvText.setVisibility(View.VISIBLE);
        tvText.setText(
                "当前时间 " + SmartDateTool.date2String(SmartDateTool.string2Date(SmartDateTool.getCurrentDate())) + "\n"
                +"当前周 " + SmartDateTool.getWeek(System.currentTimeMillis()) + "\n"
                +"是否同一天 " + SmartDateTool.isSameDay(System.currentTimeMillis()) + "\n"
                +"是否闰年 " + SmartDateTool.isLeapYear(2018) + "\n"
        );
    }

    private void goneView(){
        tvText.setVisibility(View.GONE);
        ivImage.setVisibility(View.GONE);
    }
    private void startScreenTool() {
        int width = ScreenTool.getScreenWidth(this);
        int heigh = ScreenTool.getScreenHeight(this);
        float dp = 10;
        float px = ScreenTool.dp2px(dp);
        float reDp = ScreenTool.px2dp(px);
        Bitmap bitmap = ScreenTool.snapShotWithoutStatusBar(this);

        tvText.setVisibility(View.VISIBLE);
        tvText.setText("Screen Width = " + width + "\n"
                + "Screen Heigh = " + heigh + "\n"
                + " 10 dp = " + px + "px \n"
                + px + " px = " + reDp + " dp \n");

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageBitmap(bitmap);
    }

}
