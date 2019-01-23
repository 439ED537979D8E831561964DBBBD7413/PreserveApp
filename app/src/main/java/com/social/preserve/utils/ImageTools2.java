package com.social.preserve.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.StorageUtils;


import java.io.File;
import java.io.FileOutputStream;
import com.social.preserve.R;



public class ImageTools2 {

    private static final String TAG = "ImageTools2";
    public static String getVideoPath(){
        return "";
    }
    public static String getImagePath(){
        return "";
    }
    // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageForEmptyUri(R.mipmap.default_img) // 设置图片Uri为空或是错误的时候显示的图片
//            .showImageOnLoading(R.mipmap.default_img)
//            .showImageOnFail(R.mipmap.default_img) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡
            .displayer(new FadeInBitmapDisplayer(200) )
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build(); // 构建完成


    // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private final static DisplayImageOptions nooptions = new DisplayImageOptions.Builder()
            // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡
            .displayer(new FadeInBitmapDisplayer(200) )
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build(); // 构建完成


    // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private final static DisplayImageOptions options_head = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.ic_default_avatar) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.ic_default_avatar) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡
            .displayer(new FadeInBitmapDisplayer(200) )
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build(); // 构建完成


    //显示本地,网络,R.drawable.xxx
    public static void displayLocal(ImageView imageView, int resId) {
        if (resId < 0) return;
        String url = "drawable://" + resId;
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    //带进度显示.
    public static void displayProgress(ImageView imageView, String uri, SimpleProgressListener l) {
        if (uri == null || uri.length() <= 0) return;
        if (uri.charAt(0) == '/') {
            uri = "file://" + uri;
        }
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(imageView), options,l, l);
    }

    public static void displaySize(ImageView imageView, String uri, int maxWidth, int maxHeigh) {
        if (uri == null || uri.length() <= 0) return;
        if (uri.charAt(0) == '/') {
            uri = "file://" + uri;
        }
        ImageLoader.getInstance().displayImage(uri, imageView, new ImageSize(maxWidth, maxHeigh));
    }



    //显示assets目录文件.
    public static void displayAssert(ImageView imageView, String assertPath) {
        String url = "assets://" + assertPath;
        ImageLoader.getInstance().displayImage(url, imageView, options);

    }


    public static class SimpleProgressListener implements ImageLoadingListener, ImageLoadingProgressListener {

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {

        }
    }

//    public static String getPath(){
//        return WebUrlModel.imgPrefix;
//
//    }
//
//    public static String getVideoPath(){
//        return WebUrlModel.videoPrefix;
//
//    }


    public static void showAvatar(final ImageView imageView, String uri) {

        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.ic_default_avatar);
            return;
        }
//        if (uri == null || uri.length() <= 0) return;
//        if (uri.charAt(0) == '/') {
//            uri = "file://" + uri;
//        }else if (!uri.startsWith("http")) {
//            uri = getPath() + uri + "_100";
//        }
        ImageLoader.getInstance().displayImage(uri, imageView, options_head, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options_head);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private static void makeUpShowImg(final int num, final ImageView imageView, String imageUri, final DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(imageUri, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("fuck","onLoadingFailed >> "+ imageUri);
                        if(num<3){
                            int n = num+1;
                            makeUpShowImg(n,imageView, imageUri,options);
                        }
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static void show200(final ImageView imageView, String uri) {

        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
//        if (uri.charAt(0) == '/') {
//            uri = "file://" + uri;
//        }else if (!uri.startsWith("http")) {
//            uri = getPath() + uri + "_200";
//        }
        ImageLoader.getInstance().displayImage(uri,imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static void show(String uri, final ImageView imageView) {

        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
//        if (uri.charAt(0) == '/') {
//            uri = "file://" + uri;
//        }else if (!uri.startsWith("http")) {
//            uri = getPath() + uri;
//        }
        Log.d(TAG, "show: "+uri);
        ImageLoader.getInstance().displayImage(uri,imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static void show200(final ImageView imageView, String uri, final boolean noDefImg) {

        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
//        if (uri.charAt(0) == '/') {
//            uri = "file://" + uri;
//        }else if (!uri.startsWith("http")) {
//            uri = getPath() + uri + "_200";
//        }
        if(noDefImg){
            ImageLoader.getInstance().displayImage(uri, imageView, nooptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            makeUpShowImg(1,imageView, imageUri,nooptions);
                        }
                    }, 10);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else {
            ImageLoader.getInstance().displayImage(uri, imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            makeUpShowImg(1,imageView, imageUri,options);
                        }
                    }, 10);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }
    public static void show400(final ImageView imageView, String uri) {
        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
        if (uri.charAt(0) == '/') {
            uri = "file://" + uri;
        }else {
//            uri = uri + "_400";
        }

        ImageLoader.getInstance().displayImage(uri,imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }


    public static void show800(final ImageView imageView, String uri) {
        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
        if (uri.charAt(0) == '/') {
            uri = "file://" + uri;
        }else {
//            uri = uri + "_800";
        }
        ImageLoader.getInstance().displayImage(uri,imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                Log.d(TAG, "onLoadingFailed: ");
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.d(TAG, "onLoadingComplete: ");
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static void show800Godes(final ImageView imageView, String uri){
        if (uri == null || uri.length() <= 0){
            displayLocal(imageView,R.mipmap.default_img);
            return;
        }
//        if (uri.charAt(0) == '/') {
//            uri = "file://" + uri;
//        }else if (!uri.startsWith("http")) {
//            uri = getPath() + uri + "_800";
//        }
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(uri,imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(final String imageUri, View view, FailReason failReason) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeUpShowImg(1,imageView, imageUri,options);
                    }
                }, 10);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static String saveBitMapToFile(Context context, String fileName, Bitmap bitmap, boolean isCover) {
        if(null == context || null == bitmap) {
            return null;
        }
        if(TextUtils.isEmpty(fileName)) {
            return null;
        }
        FileOutputStream fOut = null;
        try {
            File file = null;
            String fileDstPath = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 保存到sd卡
                fileDstPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "videoCover" + File.separator + fileName;
                File homeDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "videoCover" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
            } else {
                // 保存到file目录
                fileDstPath = context.getFilesDir().getAbsolutePath() + File.separator + "videoCover" + File.separator + fileName;
                File homeDir = new File(context.getFilesDir().getAbsolutePath() + File.separator + "videoCover" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdir();
                }
            }

            file = new File(fileDstPath);
            if (!file.exists() || isCover) {
                // 简单起见，先删除老文件，不管它是否存在。
                file.delete();
                fOut = new FileOutputStream(file);
                if (fileName.endsWith(".jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                }
                fOut.flush();
                bitmap.recycle();
            }

            return fileDstPath;
        } catch (Exception e) {

            return null;
        } finally {
            if(null != fOut) {
                try {
                    fOut.close();
                } catch (Exception e) {

                }
            }
        }
    }
}
