package com.social.preserve.utils;

/**
 * Created by pt198 on 15/01/2019.
 */

public class Api {
    public static final String BASE_URL="http://128.1.38.178:8080/";
    public static final String APP_UPDATE=BASE_URL+"api/upgrade";
    public static final String SHORT_VIDEO_TYPE_LIST =BASE_URL+"api/classifyInfo";
    public static final String LANDSCAPE_VIDEO_TYPE_LIST =BASE_URL+"api/videoClassifyInfo";
    public static final String SHORT_VIDEO_LIST=BASE_URL+"api/shortVideo";
    public static final String LANDSCAPE_VIDEO_LIST=BASE_URL+"api/video";
    public static final String THIRD_LOGIN=BASE_URL+"api/threePartLogin";
    public static final String WEB_URL_POLICY=BASE_URL+"page/privacy-policy.html";
    public static final String WEB_URL_TERMS_USE=BASE_URL+"page/tos.html";
    public static final String SEARCH_SHORT_VIDEO_TAGS=BASE_URL+"api/shortVideoInfoLabel";
    public static final String SEARCH_LAND_VIDEO_TAGS=BASE_URL+"api/videoInfoLabel";
    public static final String RECOMMEND_VIDEOS=BASE_URL+"api/videoList";
    public static final String REPORT_USER_INFO=BASE_URL+"api/uploadUserInfo";
    public static final String SHARE_BASE_URL="http://128.1.38.178:8080/";
    public static final String LANDVIDEO_SHARE_URL=SHARE_BASE_URL+"sharepage/video/";
    public static final String SHORTVIDEO_SHARE_URL=SHARE_BASE_URL+"sharepage/shortvideo/";
}
