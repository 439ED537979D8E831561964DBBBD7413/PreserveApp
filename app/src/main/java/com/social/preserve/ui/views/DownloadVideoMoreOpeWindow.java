package com.social.preserve.ui.views;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.utils.Config;
import com.social.preserve.utils.ScreenUtils;
import com.social.preserve.utils.ShareUtils;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * Created by pt198 on 05/11/2018.
 */

public class DownloadVideoMoreOpeWindow {
    private static final String TAG = "DownloadVideoMoreOpeWin";
    public interface OnEditListener{
        void onEdit();
    }

    private static void openAssignFolder(Activity act,String path){
        if(act==null){
            return;
        }
        File downDir=new File(Config.DOWNLOAD_STORAGE_DIR);
        if(!downDir.getParentFile().exists()){
            downDir.getParentFile().mkdirs();
        }
        if(!downDir.exists()){
            downDir.mkdirs();
        }
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        String authority = act.getPackageName() ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(act, authority, file);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "*/*");

        try {
            act.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void show(final Activity activity, View parent, final OnEditListener listener){
        final PopupWindow pop = new PopupWindow(ScreenUtils.dip2px(activity,155), ScreenUtils.dip2px(activity,105));
        View content = LayoutInflater.from(activity).inflate(R.layout.layout_down_video_more_ope, null);
        View edit = content.findViewById(R.id.ll_edit);
        View externalShow=content.findViewById(R.id.ll_external_show);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                if(listener!=null){
                    listener.onEdit();
                }
            }
        });
        externalShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                openAssignFolder(activity, Config.DOWNLOAD_STORAGE_DIR);
            }
        });

        pop.setContentView(content);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x34000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        pop.setBackgroundDrawable(dw);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.3f; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1.0f; //0.0-1.0
                activity.getWindow().setAttributes(lp);
            }
        });
        pop.showAsDropDown(parent,0,10);
    }
}
