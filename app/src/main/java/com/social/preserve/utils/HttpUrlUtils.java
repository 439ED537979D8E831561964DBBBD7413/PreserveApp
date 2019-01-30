package com.social.preserve.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Created by pt198 on 29/01/2019.
 */

public class HttpUrlUtils {
    public static HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.connect();
        return connection;

    }
    public static String getHttpFileType(String destUrl){
        try {
            URL url = new URL(destUrl);
            URLConnection conn = url.openConnection();
            Map headers = conn.getHeaderFields();
//            Set<String> keys = headers.keySet();
//            String contentType=headers.get("Content-Type");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static boolean downLoad(String url){
//        if(TextUtils.isEmpty(url)){
//            return false;
//        }
//        if(url.indexOf("/200")!=-1) {
//            url = url.substring(0, url.indexOf("/200"));
//        }
//        BufferedInputStream bis =null;
//        BufferedOutputStream bos=null;
//        try {
//            int contentLength = getConnection(url).getContentLength();
//            if (contentLength>32) {
//                InputStream is= getConnection(url).getInputStream();
//                bis = new BufferedInputStream(is);
//                File dest=new File(Config.PHOTO_STORAGE_DIR+getDigest(url)+".png");
//                if(!dest.getParentFile().exists()){
//                    dest.getParentFile().mkdirs();
//                }
//                if(!dest.exists()) {
//                    dest.createNewFile();
//                }
//                FileOutputStream fos = new FileOutputStream(dest);
//                bos= new BufferedOutputStream(fos);
//                int b = 0;
//                byte[] byArr = new byte[1024];
//                while((b= bis.read(byArr))!=-1){
//                    bos.write(byArr, 0, b);
//                }
//
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            try {
//                if(bis !=null){
//                    bis.close();
//                }
//                if(bos !=null){
//                    bos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
}
