package com.social.preserve.utils;

import android.text.TextUtils;

/**
 * Created by pt198 on 28/01/2019.
 */

public class StringUtil {
    public static String convertToLabels(String label){
        if(TextUtils.isEmpty(label)){
            return "";
        }
        String tmp[]=label.split(",");
        if(tmp!=null&&tmp.length>1){
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<tmp.length;i++) {
                sb.append("#");
                sb.append(tmp[i]+" ");
            }
            return sb.toString();
        }else{
            return "#"+label;
        }
    }
}
