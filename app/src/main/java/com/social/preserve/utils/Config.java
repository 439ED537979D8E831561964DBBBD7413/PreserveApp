package com.social.preserve.utils;

import android.os.Environment;

/**
 * Created by pt198 on 15/01/2019.
 */

public class Config {
    public static final String DOWNLOAD_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/preserve_download/";
    public static final String APKPATH = DOWNLOAD_STORAGE_DIR+"update-release.apk";
}
