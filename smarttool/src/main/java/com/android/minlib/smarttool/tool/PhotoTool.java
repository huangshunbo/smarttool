package com.android.minlib.smarttool.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.minlib.smarttool.permission.PermissionCallback;
import com.android.minlib.smarttool.permission.SmartPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author: huangshunbo
 * @Filename: PhotoTool
 * @Description: 图片获取处理类
 * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
 * @date: 2018/5/21 14:42
 */
public class PhotoTool {

    /**
     * @author: huangshunbo
     * @Filename: PhotoTool
     * @Description: 从第三方相册获取图片
     * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
     * @date: 2018/5/21 14:42
     */
    public static void openAlbumFromThrid(Activity activity, final int requestCode) {
        final WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SmartPermission.getInstance()
                .create(weakReference.get())
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestPermissionWithSetting(new PermissionCallback() {
                    @Override
                    public void onPermissionsResult(@NonNull List<String> allows, @NonNull List<String> refuses, boolean isAllAllow) {
                        if (isAllAllow) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            weakReference.get().startActivityForResult(intent, requestCode);
                        }
                    }
                });
    }

    /**
     *<br> Description: 从系统相册【所有图片】中获取图片
     *<br> Author:      huangshunbo
     *<br> Date:        2018/5/23 9:56
     */
    public static void openAlbumFromSystem(Activity activity, final int requestCode) {
        final WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SmartPermission.getInstance()
                .create(weakReference.get())
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestPermissionWithSetting(new PermissionCallback() {
                    @Override
                    public void onPermissionsResult(@NonNull List<String> allows, @NonNull List<String> refuses, boolean isAllAllow) {
                        if (isAllAllow) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
                            intent.setType("image/*");//从所有图片中进行选择
                            weakReference.get().startActivityForResult(intent, requestCode);
                        }
                    }
                });
    }

    /**
     * @author: huangshunbo
     * @Filename: PhotoTool
     * @Description: 从相机获取图片
     * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
     * @date: 2018/5/21 14:42
     */
    public static void openCamera(Activity activity, final Uri imageUri, final int requestCode) {
        final WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SmartPermission.getInstance()
                .create(weakReference.get())
                .addPermission(Manifest.permission.CAMERA)
                .requestPermissionWithSetting(new PermissionCallback() {
                    @Override
                    public void onPermissionsResult(@NonNull List<String> allows, @NonNull List<String> refuses, boolean isAllAllow) {
                        if (isAllAllow) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra("output", imageUri);
                            weakReference.get().startActivityForResult(intent, requestCode);
                        }
                    }
                });
    }

    /**
     * @author: huangshunbo
     * @Filename: PhotoTool
     * @Description: 将图片进行裁切
     * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
     * @date: 2018/5/21 14:42
     */

    public static void cropImageUri(Activity activity, Uri orgUri, Uri dstUri, int requestCode) {
        cropImageUri(activity, orgUri, dstUri, 1, 1, 300, 300, requestCode);
    }

    @SuppressLint("WrongConstant")
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        activity.grantUriPermission(activity.getPackageName(), desUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.EXTRA_DOCK_STATE_DESK);
            intent.addFlags(Intent.EXTRA_DOCK_STATE_CAR);
        }

        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);//黑边
        intent.putExtra("output", desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }


    public static void compressImageView(ImageView imageView, String filePath) {
        Bitmap bmp = getSmallBitmap(filePath, imageView.getContext(), imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        imageView.setImageBitmap(bmp);
    }

    public static Bitmap getSmallBitmap(String imgPath, Context context, int pixelH, int pixelW) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) {  //循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
