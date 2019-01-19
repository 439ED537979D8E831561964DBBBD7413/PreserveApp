package com.social.preserve.utils;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.instagram.Instagram;
import cn.sharesdk.twitter.Twitter;

/**
 * Created by pt198 on 23/08/2018.
 */

public class ThirdLoginUtils {

    public static final int LOGIN_TYPE_TWITTER = 1;
    public static final int LOGIN_TYPE_FACEBOOK = 2;
    public static final int LOGIN_TYPE_GOOGLEPLUS = 3;
    public static final int LOGIN_TYPE_INSTAGRAM = 4;

    public static void thirdAuth(int loginType, PlatformActionListener listener) {
        Platform plat = null;
        switch (loginType) {
            case LOGIN_TYPE_FACEBOOK:
                plat = ShareSDK.getPlatform(Facebook.NAME);
                break;
            case LOGIN_TYPE_TWITTER:
                plat = ShareSDK.getPlatform(Twitter.NAME);
                break;
            case LOGIN_TYPE_GOOGLEPLUS:
                plat = ShareSDK.getPlatform(GooglePlus.NAME);
                break;
            case LOGIN_TYPE_INSTAGRAM:
                plat = ShareSDK.getPlatform(Instagram.NAME);
                break;
        }
        plat.setPlatformActionListener(listener);//授权回调监听，监听oncomplete，onerror，oncancel三种状态
        if (plat.isAuthValid()) {
            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
//            Toast.makeText(App.getInstance(), "已经授权过了", Toast.LENGTH_SHORT).show();
            plat.removeAccount(true);//移除授权状态和本地缓存，下次授权会重新授权
//            return;
        }
        if(loginType==LOGIN_TYPE_GOOGLEPLUS) {
            plat.SSOSetting(true);//SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        }else{
            plat.SSOSetting(false);
        }
        //plat.authorize();	//要功能，不要数据

        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }
}
