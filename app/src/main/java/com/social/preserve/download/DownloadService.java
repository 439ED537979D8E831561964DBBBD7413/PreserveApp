package com.social.preserve.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.social.preserve.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by pt198 on 28/09/2018.
 */

public class DownloadService extends Service implements DownloadManager.OnDownloadListener{
    private Notification mNotification;
    NotificationManager mManager;
    private Map<String,Notification> mNotificationMap=new HashMap<>();
    private int mIdIndex;
    private Map<String,Integer> mNotificationIds=new HashMap<>();
    private Handler mHandler;
    private static final String CHANNEL_ID="DownloadService";
    private static final String CHANNEL_NAME="download";
    private static final String TAG = "DownloadService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            mHandler=new Handler();
            mManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
//                        NotificationManager.IMPORTANCE_HIGH);
//                mManager.createNotificationChannel(channel);
//                Notification.Builder builder=new Notification.Builder(getApplicationContext(), CHANNEL_ID);
//                builder.setSmallIcon(R.mipmap.ic_launcher) //设置通知图标
//                        .setContentTitle("")//设置通知标题
//                        .setContentText("")//设置通知内容
//                        .setAutoCancel(false) //用户触摸时，自动关闭
//                        .setOngoing(true);//设置处于运行状态
//                mNotification = new Notification.Builder(getApplicationContext(), CHANNEL_ID).build();
                mNotification = new Notification.Builder(this)
                        .setContentTitle("") .setContentText("")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setChannelId(CHANNEL_ID)
                        .build();
                startForeground(1, mNotification);
            }
            Log.d(TAG, "DownloadManager.getInstace().init: ");
            DownloadManager.getInstace().init();
            DownloadManager.getInstace().registerDownloadListener(this);
        }catch (Exception e){
            Log.e("download_service","DownloadService"+e.getMessage());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstace().release();
        DownloadManager.getInstace().unregisterDownloadListener(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(String taskId,String name) {
        Notification notification=createNotification(true,name+" "+getString(R.string.label_downloading));
        mManager.notify(mIdIndex,notification);
        mNotificationIds.put(taskId,mIdIndex);
        mNotificationMap.put(taskId,notification);
        mIdIndex++;
    }

    @Override
    public void onComplete(String taskId,final String path,String name) {
        Notification notification=createNotification(true,name+" "+getString(R.string.label_download_finish));
        if(notification!=null) {
            int id=mNotificationIds.get(taskId);
            mManager.notify(id, notification);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DownloadService.this, getResources().getString(R.string.video_saved_in)+" "+path, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFail(String taskId, String error,String name) {

        Notification notification=createNotification(true,name+" "+getString(R.string.label_download_fail));
        if(notification!=null) {
            int id=mNotificationIds.get(taskId);
            mManager.notify(id, notification);
        }
    }

    @Override
    public void onProgress(String taskId, int progress,String name) {
//        Notification notification=createNotification(true,name+" "+getString(R.string.label_downloading)+progress+"%");
//        if(notification!=null) {
//            int id=mNotificationIds.get(taskId);
//            mManager.notify(id, notification);
//        }
    }
    private Notification createNotification(boolean cancelable,String content){
        Notification notification=null;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setContentTitle("") .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();

        }else{
            NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher) //设置通知图标
                    .setContentTitle("")//设置通知标题
                    .setContentText(content)//设置通知内容
                    .setAutoCancel(cancelable); //用户触摸时，自动关闭
            notification = builder.build();
        }
        return notification;
    }

}
