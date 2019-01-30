package com.social.preserve.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;


import com.social.preserve.App;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.utils.Config;
import com.social.preserve.utils.MD5;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import java.util.Collections;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.social.preserve.utils.Config.VIDEO_SUFFIX;

/**
 * Created by pt198 on 28/09/2018.
 */

public class DownloadManager {
    private boolean mLoop;
    private LoopThread mTh;
    private Map<String,DownloadTask> mAllTasks= Collections.synchronizedMap(new LinkedHashMap<String, DownloadTask>());
    private Map<String,DownloadTask> mDownloadingTasks = Collections.synchronizedMap(new LinkedHashMap<String, DownloadTask>());
    private Map<String,DownloadTask> mDownloadFinishedTasks = Collections.synchronizedMap(new LinkedHashMap<String, DownloadTask>());
    private Map<String,DownloadTask> mDownloadFailTasks = Collections.synchronizedMap(new LinkedHashMap<String, DownloadTask>());
    private Map<String,DownloadTask> mWaitingTasks= Collections.synchronizedMap(new LinkedHashMap<String, DownloadTask>());
    private List<OnDownloadListener> mListeners=new ArrayList<>();
    private static final int MAX_TASK=1;
    private static final String TAG = "DownloadManager";
    private DownloadManager(){

    }
    public void init(){
        File downDir=new File(Config.DOWNLOAD_STORAGE_DIR);
        if(!downDir.getParentFile().exists()){
            downDir.getParentFile().mkdirs();
        }
        if(!downDir.exists()){
            downDir.mkdirs();
        }
        mLoop=true;
        startLoop();
        List<PreserveVideo> landVideos=VideoManager.getInstace().getLandVideos();
        List<PreserveVideo> shortVideos=VideoManager.getInstace().getShortVideos();
        for(PreserveVideo video:landVideos){
            if(VideoManager.getInstace().isDownloadSucc(video.getId())){
                DownloadTask task=new DownloadTask(video.getVideoUrl().get(0),video.getId(),"","","",false);
                mDownloadFinishedTasks.put(video.getId(),task);
            }
        }
        for(PreserveVideo video:shortVideos){
            if(VideoManager.getInstace().isDownloadSucc(video.getId())){
                DownloadTask task=new DownloadTask(video.getVideoUrl().get(0),video.getId(),"","","",true);
                mDownloadFinishedTasks.put(video.getId(),task);
            }
        }
    }
    public void registerDownloadListener(OnDownloadListener listener){
        if(listener!=null){
            mListeners.add(listener);
        }
    }

    public void unregisterDownloadListener(OnDownloadListener listener){
        if(listener!=null){
            mListeners.remove(listener);
        }
    }
    public void submitDownloadVideoTask(String videoId,String filepath, String filaName, String coverPath, boolean isShortVideo){


        DownloadTask task = new DownloadTask(filepath, videoId, filaName, "", coverPath,isShortVideo);
        mWaitingTasks.put(task.getId(), task);
        mAllTasks.put(task.getId(), task);
        for(OnDownloadListener listener:mListeners){
            if(listener!=null){
                listener.onStart(task.getId(),task.getFileName());
            }
        }
        PreserveVideo video=new PreserveVideo();
        video.setCover(task.getCoverPath());
        List<String> urls=new ArrayList<>();
        urls.add(Config.DOWNLOAD_STORAGE_DIR+task.getId()+VIDEO_SUFFIX);
        video.setVideoUrl(urls);
        video.setId(videoId);
        VideoManager.getInstace().saveVideoInfoToDb(video,task.isShortVideo());
        synchronized (mTh) {
            mTh.notify();
        }
    }
    private class LoopThread extends Thread{
        @Override
        public void run() {
            while(mLoop){
                Log.d(TAG, "run: mWaitingTasks.size "+mWaitingTasks.size()+",mDownloadingTasks.size "+mDownloadingTasks.size());
                if(mWaitingTasks.size()>0&& mDownloadingTasks.size()<MAX_TASK){
                    String id=null;
                    //get the first task in waitingTask
                    for(Map.Entry<String,DownloadTask> entry:mWaitingTasks.entrySet()){
                        id=entry.getKey();
                        break;
                    }
                    DownloadTask task=mWaitingTasks.get(id);
                    mDownloadingTasks.put(id,task);
                    mWaitingTasks.remove(id);
                    Log.d(TAG, "run: id "+id+",mWaitingTasks.size "+mWaitingTasks.size());
                    doDownload(task);
                }else{
                    if(mWaitingTasks.size()<=0){
                        synchronized (this){
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        try {
                            Thread.sleep(300L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }



    private void doDownload(final DownloadTask task){
        Log.d(TAG, "doVideoUpload: "+task.getPath()+","+task.getFileName());

        RequestParams params = new RequestParams(task.getPath());
        params.setAutoRename(true);//断点下载
        params.setSaveFilePath(Config.DOWNLOAD_STORAGE_DIR+task.getId()+VIDEO_SUFFIX);
        x.http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.d(TAG, "onFail: ");
                mDownloadingTasks.remove(task.getId());
                mDownloadFailTasks.put(task.getId(),task);
                for(OnDownloadListener listener:mListeners){
                    if(listener!=null){
                        listener.onFail(task.getId(),arg0.toString(),task.getFileName());
                    }
                }
                synchronized (mTh){
                    mTh.notify();
                }

            }

            @Override
            public void onFinished() {


            }

            @Override
            public void onSuccess(File file) {

                Log.d(TAG, "onComplete: path "+task.getPath()+",taskId "+task.getId());
                task.setProgress(100);
                mDownloadingTasks.remove(task.getId());
                mDownloadFinishedTasks.put(task.getId(),task);
                for(OnDownloadListener listener:mListeners){
                    if(listener!=null){
                        listener.onComplete(task.getId(),task.getPath(),task.getFileName());
                    }
                }
                VideoManager.getInstace().updateDownloadSuccToDb(task.getId());
                synchronized (mTh){
                    mTh.notify();
                }

            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {
                long progress=arg1*100/arg0;
                Log.d(TAG, "onProgress: "+progress);
                task.setProgress((int)progress);
                for(OnDownloadListener listener:mListeners){
                    if(listener!=null){
                        listener.onProgress(task.getId(),(int)progress,task.getFileName());
                    }
                }
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onWaiting() {

            }
        });

    }

    public void release(){
        mLoop=false;
        if(mTh!=null&&!mTh.isInterrupted()){
            mTh.interrupt();
        }
    }

    private void startLoop(){
        mTh=new LoopThread();
        mTh.start();
    }

    private static class SingleInstace{
        public static DownloadManager instance=new DownloadManager();
    }
    public static DownloadManager getInstace(){
        return SingleInstace.instance;
    }


    public interface OnDownloadListener {
        void onStart(String taskId,String name);
        void onComplete(String taskId, String path,String name);
        void onFail(String taskId, String error,String name);
        void onProgress(String taskId, int progress,String name);
    }

    /**
     *
     * @param taskId 对文件路径进行MD5加密后得到的值
     */
    public int getDownloadProgress(String taskId){
        if(mDownloadingTasks.containsKey(taskId)){
            return mDownloadingTasks.get(taskId).getProgress();
        }
        return 0;
    }

    public Map<String, DownloadTask> getDownloadingTasks() {
        return mDownloadingTasks;
    }

    public Map<String, DownloadTask> getmWaitingTasks() {
        return mWaitingTasks;
    }

    public Map<String, DownloadTask> getDownloadFailTasks() {
        return mDownloadFailTasks;
    }

    public Map<String, DownloadTask> getDownloadFinishedTasks() {
        return mDownloadFinishedTasks;
    }
}
