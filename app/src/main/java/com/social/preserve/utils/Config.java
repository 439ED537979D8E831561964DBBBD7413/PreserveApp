package com.social.preserve.utils;

import android.os.Environment;

/**
 * Created by pt198 on 15/01/2019.
 */

public class Config {
    public static final String DOWNLOAD_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/preserve/download/";
    public static final String APKPATH = DOWNLOAD_STORAGE_DIR+"update-release.apk";


    //third library config
    public static final String TALKING_DATA_APP_ID="B3AC8095F31B4F31A4ED88690225E81D";
    public static final String TALKING_DATA_CHANNEL_ID="play.google.com";

}
