package com.social.preserve.utils;

/**
 * Created by pt198 on 15/01/2019.
 */

public class Api {
    public static final String BASE_URL="http://192.168.40.104:8080/";
    public static final String APP_UPDATE=BASE_URL+"shortvideo-manager/api/upgrade";
    public static final String SHORT_VIDEO_TYPE_LIST =BASE_URL+"shortvideo-manager/api/classifyInfo";
    public static final String LANDSCAPE_VIDEO_TYPE_LIST =BASE_URL+"shortvideo-manager/api/videoClassifyInfo";
    public static final String SHORT_VIDEO_LIST=BASE_URL+"shortvideo-manager/api/shortVideo";
    public static final String LANDSCAPE_VIDEO_LIST=BASE_URL+"shortvideo-manager/api/video";
    public static final String THIRD_LOGIN=BASE_URL+"shortvideo-manager/api/threePartLogin";
    public static final String WEB_URL_POLICY="";
    public static final String WEB_URL_TERMS_USE="";
    public static final String SEARCH_TAGS=BASE_URL+"shortvideo-manager/api/videoInfoLabel";
    public static final String RECOMMEND_VIDEOS=BASE_URL+"shortvideo-manager/api/videoList";
}
