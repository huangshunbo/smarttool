#### Summary
---
本基础库是一个工具库，包含权限请求、图片处理、屏幕信息获取、Shell命令、日期工具、网络判断工具、正则工具、StatusBar工具、系统信息

#### Getting Started
---
加入依赖
```Java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
  implementation 'com.github.huangshunbo:smarttool:lastest.release'
}
```

##### 权限请求姿势
```Java
private static String[] PERMISSIONS_STORAGE = {
    Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
};

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
```

##### PhotoTool获取图片并裁剪的姿势
获取图片的三种途径：从系统相册、从第三方相册、拍照
```Java
//从系统相册【所有图片】中获取图片
PhotoTool.openAlbumFromSystem(this,PICTURE_CODE);
//从第三方相册获取图片
PhotoTool.openAlbumFromThrid(this,ALBUM_CODE);
//拍照
Uri uri = FileProvider.getUriForFile(this,"com.android.minlib.samplesimplewidget",tmpFile);
PhotoTool.openCamera(PhotoToolActivity.this,uri ,CAMERA_CODE);
```
在Activity中加入onActivityResult 来获取照片
```Java
if(resultCode == Activity.RESULT_OK){
    if(requestCode == CAMERA_CODE){//从拍照途径获取的照片
        Uri org = FileProvider.getUriForFile(this,"com.android.minlib.samplesimplewidget",tmpFile);
    }else if(requestCode == ALBUM_CODE){//从第三方相册获取的照片
        Uri org = data.getData();
    }else if(requestCode == PICTURE_CODE){//从系统相册获取的照片
        Uri org = data.getData();
    }
}
 ```
获取照片后我们还可以进行裁剪
发起请求
```Java
//原始待处理图片文件
File oriFile = new File(ORI_PATH)'
//裁剪后图片保存的文件
File dstFile = new File(DST_PATH);
//把原始图片转换成URI对象
Uri org = FileProvider.getUriForFile(this,"com.android.minlib.samplesimplewidget",orgFile);
//把目标文件转换成URI对象【这两种转换方式不能错，否则裁剪拿不到最终正确结果，原因不详】
Uri dst = Uri.fromFile(dstFile);
PhotoTool.cropImageUri(this,org,dst,COMPRESS_CODE);
```
最后返回还是会在onActivityResult中进行回调

根据ImageView大小对图片进行压缩并设置到ImageView中
```Java
PhotoTool.compressImageView(imageView,filePath);
```

##### StatusBarTool 使用姿势
```Java
StatusBarTool.noTitle(this);//去掉ActionBar或ToolBar的Title
StatusBarTool.FLAG_FULLSCREEN(this);//全屏
FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1,-1);
lp.topMargin = StatusBarTool.getStatusBarHeight(this);//获取状态栏的高度
mListView.setLayoutParams(lp);//设置完颜色会入侵状态栏，所以需要把状态栏的高度margin抵消掉
StatusBarTool.setStatusBarColor(this,color,false);//设置状态栏颜色
StatusBarTool.StatusBarLightMode(this);//改变状态栏字体颜色
```

##### ScreenTool
```Java
ScreenTool.getScreenWidth(this);
ScreenTool.getScreenHeight(this);
ScreenTool.dp2px(dp);
ScreenTool.px2dp(px);
ScreenTool.snapShotWithoutStatusBar(this);//截屏
```

##### SmartDataTool
```Java
//转换型
getCurrentDate 获取当前时间的字符串 格式 yyyy-MM-dd HH:mm:ss
getCurrentDate(String pattern) 根据指定格式获取表示当前时间的字符串
format(Date date) 格式化时间 格式 yyyy-MM-dd HH:mm:ss
format(Date date, String pattern) 根据pattern指定格式格式化时间
millis2String(long millis) 将时间戳转换为时间 格式 yyyy-MM-dd HH:mm:ss
millis2String(long millis, String pattern) 根据pattern指定格式将时间戳转换为时间
string2Millis(String time) 将格式化的时间转换为时间戳 格式 yyyy-MM-dd HH:mm:ss
string2Millis(String time, String pattern) 将指定格式的时间转换为时间戳
string2Date(String time) 将格式化的时间转换为Date对象 格式 yyyy-MM-dd HH:mm:ss
string2Date(String time, String pattern) 将指定pattern格式的时间字符串转换为Date对象
utcString2Date(String time) 将时间字符串转换为Date对象 格式为UTC
date2String(Date date) 将Date对象转换为时间字符串 格式 yyyy-MM-dd HH:mm:ss
date2String(Date date, String pattern) 将Date对象转换为指定格式的时间字符串
date2Millis(Date date) 将Date对象转换为时间戳
millis2Date(long millis) 将时间戳转换为Date对象

//判断计算型
DateDifference getTwoDataDifference(Date date) 计算date与当前时间的时差
DateDifference getTwoDataDifference(String str) 计算str与当前时间的时差 str时间字符串的格式为 yyyy-MM-dd HH:mm:ss
DateDifference getTwoDataDifference(String str1, String str2) 计算两个日期的时间差 格式 yyyy-MM-dd HH:mm:ss
DateDifference getTwoDataDifference(Date date1, Date date2) 计算两个日期的时间差
isSameDay(String time) 判断时间字符串是否为今天 格式 yyyy-MM-dd HH:mm:ss
isSameDay(String time, String pattern) 判断指定格式的时间字符串是否为今天
isSameDay(Date date) 判断date是否为今天
isSameDay(long millis) 根据时间戳判断是否为今天
boolean isLeapYear(String time) 判断时间字符串是否为闰年 格式 yyyy-MM-dd HH:mm:ss
isLeapYear(String time, String pattern) 判断指定格式的时间字符串是否为闰年
isLeapYear(Date date) 判断date是否为闰年
isLeapYear(long millis) 判断时间戳是否为闰年
isLeapYear(int year) 根据year年份判断是否为闰年
getWeek(String time) 获取星期 格式 yyyy-MM-dd HH:mm:ss
getWeek(String time, String pattern) 获取星期
getWeek(Date date) 获取星期
getWeek(long millis) 获取星期
```
##### SmartNetworkTool
所需权限
```Java
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
提供的方法
```Java
SmartNetworkTool.isWifiAvailable(this); //WIFI是否可用
SmartNetworkTool.ping(); //网络是否畅通，通过ping baidu的方式
SmartNetworkTool.is4G(this); // 是否是4G网络
SmartNetworkTool.getNetworkType(this) //网络类型
SmartNetworkTool.getNetworkOperatorName(this); // 运营商
SmartNetworkTool.getIPAddress(true); //获取本机IP
SmartNetworkTool.setWifiEnabled(true,this); //打开或关闭WIFI
SmartNetworkTool.openWirelessSettings(this); //打开WIF设置界面
```


##### SmartRegexTool
```Java
phoneNoHide(String phone) //手机号码打码
cardIdHide(String cardId) //银行卡打码
idHide(String id) //身份证打码
checkUserName(String username) //校验用户名  字母、数字、下划线
checkNickName(String nickname) //校验昵称 字母、数字、汉字、下划线
checkPassword(String password) //简单密码校验 6-16位、字母、数字、字符
checkVehicleNo(String vehicleNo) //是否为车牌
checkIdCard(String idCard) //验证身份证号码
checkMobile(String mobile) //验证手机号码
checkPhone(String phone) //验证固定电话
checkEmail(String email) //验证邮箱
checkDigit(String digit) //验证整数(正整数 负整数)
checkDecimals(String decimals) //验证整数和浮点数（正负整数和正负浮点数）
checkChinese(String chinese) //验证中文
checkURL(String url) //验证URL
checkBankCard(String cardId) //验证银行卡卡号
```
##### SystemInfoTool
```Java
getSystemVersion() //获取系统版本
getPhoneBrand() //获取手机厂商
getPhoneModel() //获取手机型号
getImei(Context context) //获取IMEI
getAppVersion(Context context) //获取应用版本
getUUID(Context context) //获取UUID
getPackageInfo(Context context) //获取包信息
getPackageInfo(String pkgName,Context context) //获取包信息
startApp(String packageName,Context context) //打开app
isInstallApp(String packageName,Context context) //判断app是否已经安装
installApk(File file,Context context)  //安装apk
```

#### Known Issues
---
暂时没有收到任何反馈，有任何疑问或需求，可提issue。
#### Support
---
黄顺波
