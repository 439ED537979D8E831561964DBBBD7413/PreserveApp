package com.social.preserve.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.instagram.Instagram;
import cn.sharesdk.whatsapp.WhatsApp;


/**
 * Created by pt198 on 24/08/2018.
 */

public class ShareUtils {
    public static final String SHARE_TYPE_FACEBOOK = "1";
    public static final String SHARE_TYPE_TWITTER = "2";
    public static final String SHARE_TYPE_INSTAGRAM = "3";
    public static final String SHARE_TYPE_WHATSAPP = "4";
    private static final String TAG = "ShareUtils";
    public static final String DEFAULT_SHARE_IMG_URL="https://res.imhotok.com/share_001.png";
    public static void showShare(String shareType, Context context, String title, String content, String targetPath, String imagePath,String videoUrl,String imageUrl, PlatformActionListener platformActionListener) {

        Platform.ShareParams sp = null;
        Platform platform = null;
        switch (shareType) {
            case SHARE_TYPE_FACEBOOK:
                sp = new Facebook.ShareParams();

                platform = ShareSDK.getPlatform(Facebook.NAME);
                //文本内容

//                sp.setUrl("http://v.yinyuetai.com/video/3303109?f=SY-MKDT-MVSB-1");

                //分享图片
                if(!TextUtils.isEmpty(videoUrl)) {
                    sp.setShareType(Platform.SHARE_VIDEO);//非常重要：一定要设置分享属性
                    sp.setUrl(ImageTools2.getImagePath() +videoUrl);
                }else if(!TextUtils.isEmpty(imageUrl)) {
                    sp.setUrl(ImageTools2.getImagePath() +imageUrl+"_400");
                }else{
                    sp.setUrl(DEFAULT_SHARE_IMG_URL);
                }
//                Log.d(TAG, "showShare: content "+content);
//                sp.setText(content);
                break;
//            case SHARE_TYPE_TWITTER:
//                sp = new Twitter.ShareParams();
//
//                platform = ShareSDK.getPlatform(Twitter.NAME);
//                break;
            case SHARE_TYPE_INSTAGRAM:
                sp = new Instagram.ShareParams();
                platform = ShareSDK.getPlatform(Instagram.NAME);
                //文本内容
                sp.setTitle(title);
                sp.setText(content);
                //分享图片
                if(!TextUtils.isEmpty(targetPath)) {
                    sp.setFilePath(targetPath);
                }else if(!TextUtils.isEmpty(imagePath)){
                    sp.setImagePath(imagePath);
                }else{
                    sp.setImageUrl(DEFAULT_SHARE_IMG_URL);
                }
                break;
            case SHARE_TYPE_WHATSAPP:
                sp = new WhatsApp.ShareParams();
                platform = ShareSDK.getPlatform(WhatsApp.NAME);
                //文本内容
//                sp.setTitle(title);
//                sp.setText(content);
                //分享图片
                if(!TextUtils.isEmpty(targetPath)) {
                    sp.setFilePath(targetPath);
                }else if(!TextUtils.isEmpty(imagePath)){
                    sp.setImagePath(imagePath);
                }else{
                    sp.setImageUrl(DEFAULT_SHARE_IMG_URL);
                }

                break;
        }

        platform.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        // 执行分享
        platform.share(sp);
    }


    public static void simpleShare(String shareType, Activity act, String title, String content, String url, String imgUrl, PlatformActionListener listener) {
        Platform.ShareParams sp = null;
        Platform platform = null;
        switch (shareType) {
            case SHARE_TYPE_FACEBOOK:
                sp = new Facebook.ShareParams();
                platform = ShareSDK.getPlatform(Facebook.NAME);
                //文本内容
                sp.setTitle(title);
                sp.setUrl(url);
                break;
            case SHARE_TYPE_INSTAGRAM:
                sp = new Instagram.ShareParams();
                platform = ShareSDK.getPlatform(Instagram.NAME);
                sp.setTitle(title);
                //文本内容
                sp.setImageUrl(imgUrl);
                sp.setUrl(url);
                break;
            case SHARE_TYPE_WHATSAPP:
                sp = new WhatsApp.ShareParams();
                platform = ShareSDK.getPlatform(WhatsApp.NAME);
                sp.setTitle(title);
                //文本内容
                sp.setImageUrl(imgUrl);

                break;
        }

        platform.setPlatformActionListener(listener); // 设置分享事件回调
        // 执行分享
        platform.share(sp);
    }

    public static void shareFaceBook(Activity act, String title, String content, String url, PlatformActionListener listener) {
//        Platform.ShareParams sp = null;
//        Platform platform = null;
//        sp = new Facebook.ShareParams();
//        platform = ShareSDK.getPlatform(Facebook.NAME);
//        sp.setUrl(url);
//
//        platform.setPlatformActionListener(listener); // 设置分享事件回调
//        platform.SSOSetting(false);
//        // 执行分享
//        platform.share(sp);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,url );
        act.startActivity(Intent.createChooser(sendIntent, "分享"));
    }
}
