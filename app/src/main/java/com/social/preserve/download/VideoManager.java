package com.social.preserve.download;

import android.util.Log;

import com.social.preserve.App;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.threadpool.FixedThreadPool;
import com.social.preserve.utils.PreferencesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pt198 on 15/01/2019.
 */

public class VideoManager {
    private List<PreserveVideo> mShortVideos=new ArrayList<>();
    private List<PreserveVideo> mLandVideos=new ArrayList<>();
    PreferencesHelper mShortVideoIdHelper;
    PreferencesHelper mLandVideoIdHelper;
    PreferencesHelper mVideoCoverHelper;
    PreferencesHelper mVideoPathHelper;
    PreferencesHelper mVideoNameHelper;
    private List<PreserveVideo> mShortFavVideos=new ArrayList<>();
    private List<PreserveVideo> mLandFavVideos=new ArrayList<>();
    PreferencesHelper mShortFavVideoIdHelper;
    PreferencesHelper mLandFavVideoIdHelper;
    PreferencesHelper mVideoFavCoverHelper;
    PreferencesHelper mVideoFavPathHelper;
    PreferencesHelper mVideoFavNameHelper;

    PreferencesHelper mVideoDescHelper;
    PreferencesHelper mVideoDownloadSuccHelper;
    private static final String TAG = "VideoManager";
    private static class SingleInstace{
        public static VideoManager instance=new VideoManager();
    }
    public static VideoManager getInstace(){
        return VideoManager.SingleInstace.instance;
    }
    private VideoManager(){

    }

    public void initData(){
        mShortVideos.clear();
        mLandVideos.clear();
        mShortFavVideos.clear();
        mLandFavVideos.clear();
        mShortVideoIdHelper=new PreferencesHelper(App.getInstance(),"shortvideoIdHelper");
        mLandVideoIdHelper=new PreferencesHelper(App.getInstance(),"landvideoIdHelper");
        mVideoCoverHelper=new PreferencesHelper(App.getInstance(),"videoCoverHelper");
        mVideoPathHelper=new PreferencesHelper(App.getInstance(),"videoPathHelper");
        mVideoNameHelper=new PreferencesHelper(App.getInstance(),"videoNameHelper");
        mShortFavVideoIdHelper=new PreferencesHelper(App.getInstance(),"shortfavvideoIdHelper");
        mLandFavVideoIdHelper=new PreferencesHelper(App.getInstance(),"landfavvideoIdHelper");
        mVideoFavCoverHelper=new PreferencesHelper(App.getInstance(),"videofavCoverHelper");
        mVideoFavPathHelper=new PreferencesHelper(App.getInstance(),"videofavPathHelper");
        mVideoFavNameHelper=new PreferencesHelper(App.getInstance(),"videofavNameHelper");

        mVideoDescHelper=new PreferencesHelper(App.getInstance(),"videoDescHelper");
        mVideoDownloadSuccHelper=new PreferencesHelper(App.getInstance(),"videoDownSuccHelper");
        Set<String> ids=mShortVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: ids "+ids.toString());
        for(String id:ids){
            String cover=mVideoCoverHelper.getValue(id);
            Set<String> paths=mVideoPathHelper.getVideoPaths(id);
            String name=mVideoNameHelper.getValue(id);
            String desc=mVideoDescHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            List<String> urls=new ArrayList<>();
            for(String tmp:paths){
                urls.add(tmp);
            }
            video.setId(id);
            video.setVideoUrl(urls);
            video.setCover(cover);
            video.setPublisher(name);
            video.setDescribed(desc);
            Log.d(TAG, "mShortVideos: id "+id+",path "+urls+",cover "+cover);
            mShortVideos.add(video);
        }
        Set<String> landIds=mLandVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: landIds "+landIds.toString());
        for(String id:landIds){
            String cover=mVideoCoverHelper.getValue(id);
            Set<String> paths=mVideoPathHelper.getVideoPaths(id);
            String name=mVideoNameHelper.getValue(id);
            String desc=mVideoDescHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            List<String> urls=new ArrayList<>();
            for(String tmp:paths){
                urls.add(tmp);
            }
            video.setId(id);
            video.setVideoUrl(urls);
            video.setCover(cover);
            video.setPublisher(name);
            video.setDescribed(desc);
            Log.d(TAG, "mLandVideos: id "+id+",path "+urls+",cover "+cover);
            mLandVideos.add(video);
        }


        Set<String> favids=mShortFavVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: favids= "+favids.toString());
        for(String id:favids){
            String cover=mVideoFavCoverHelper.getValue(id);
            Set<String> paths=mVideoFavPathHelper.getVideoPaths(id);
            String name=mVideoFavNameHelper.getValue(id);
            String desc=mVideoDescHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            List<String> urls=new ArrayList<>();
            for(String tmp:paths){
                urls.add(tmp);
            }
            video.setId(id);
            video.setVideoUrl(urls);
            video.setCover(cover);
            video.setPublisher(name);
            video.setDescribed(desc);
            Log.d(TAG, "mShortFavVideos: id "+id+",path "+urls+",cover "+cover);
            mShortFavVideos.add(video);
        }
        Set<String> landFavIds=mLandFavVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: landFavIds= "+landFavIds.toString());
        for(String id:landFavIds){
            String cover=mVideoFavCoverHelper.getValue(id);
            Set<String> paths=mVideoFavPathHelper.getVideoPaths(id);
            String name=mVideoFavNameHelper.getValue(id);
            String desc=mVideoDescHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            List<String> urls=new ArrayList<>();
            for(String tmp:paths){
                urls.add(tmp);
            }
            video.setId(id);
            video.setVideoUrl(urls);
            video.setCover(cover);
            video.setPublisher(name);
            video.setDescribed(desc);
            Log.d(TAG, "mLandFavVideos: id "+id+",path "+urls+",cover "+cover);
            mLandFavVideos.add(video);
        }
    }
    public void saveVideoInfoToDb(PreserveVideo video,boolean isShortVideo){
        Set<String> ids=null;
        if(isShortVideo) {
            ids=mShortVideoIdHelper.getCacheVideoIds();
        }else{
            ids=mLandVideoIdHelper.getCacheVideoIds();
        }
        for(String id:ids){
            if(id!=null&&id.equals(video.getId())){
                return;
            }
        }
        mVideoCoverHelper.setValue(video.getId(),video.getCover());
        mVideoPathHelper.setVideoPaths(video.getId(),video.getVideoUrl());
        mVideoNameHelper.setValue(video.getId(),video.getPublisher());
        mVideoDescHelper.setValue(video.getId(),video.getDescribed());
        if(isShortVideo) {
            mShortVideoIdHelper.setVideoIDs(video.getId());
            mShortVideos.add(video);
        }else{
            mLandVideoIdHelper.setVideoIDs(video.getId());
            mLandVideos.add(video);
        }
    }
    public void updateDownloadSuccToDb(String videoId){
        mVideoDownloadSuccHelper.setValue(videoId,"true");
    }
    public boolean isDownloadSucc(String videoId){
        return "true".equals(mVideoDownloadSuccHelper.getValue(videoId));
    }
    public void addVideoToFav(PreserveVideo video,boolean isShortVideo){
        if(isShortVideo){
            mShortFavVideoIdHelper.setVideoIDs(video.getId());
            mShortFavVideos.add(video);
            Log.d(TAG, "addVideoToFav: "+mShortFavVideoIdHelper.getVideoIdList());
        }else{
            mLandFavVideoIdHelper.setVideoIDs(video.getId());
            mLandFavVideos.add(video);
        }
        mVideoFavCoverHelper.setValue(video.getId(),video.getCover());
        mVideoFavPathHelper.setVideoPaths(video.getId(),video.getVideoUrl());
        mVideoFavNameHelper.setValue(video.getId(),video.getPublisher());
        mVideoDescHelper.setValue(video.getId(),video.getDescribed());
    }

    public List<PreserveVideo> getShortFavVideos() {
        return mShortFavVideos;
    }

    public List<PreserveVideo> getShortVideos() {
        return mShortVideos;
    }

    public List<PreserveVideo> getLandFavVideos() {
        return mLandFavVideos;
    }

    public List<PreserveVideo> getLandVideos() {
        return mLandVideos;
    }
    public void delVideoFromFav(PreserveVideo video,boolean isShortVideo){
        if(isShortVideo){
            for(int i=0;i<mShortFavVideos.size();i++){
                PreserveVideo tmp=mShortFavVideos.get(i);
                if(tmp.getId().equals(video.getId())){
                    mShortFavVideos.remove(tmp);
                    Set<String> list=mShortFavVideoIdHelper.getVideoIdList();
                    list.remove(tmp.getId());
                    mShortFavVideoIdHelper.setVideoIDs(list);
                    break;
                }
            }

        }else{
            for(int i=0;i<mLandFavVideos.size();i++){
                PreserveVideo tmp=mLandFavVideos.get(i);
                if(tmp.getId().equals(video.getId())){
                    mLandFavVideos.remove(tmp);
                    Set<String> list=mLandFavVideoIdHelper.getVideoIdList();
                    list.remove(tmp.getId());
                    mShortFavVideoIdHelper.setVideoIDs(list);
                    break;
                }
            }
        }
        mVideoFavCoverHelper.remove(video.getId());
        mVideoFavPathHelper.remove(video.getId());
        mVideoFavNameHelper.remove(video.getId());
        mVideoDescHelper.remove(video.getId());
    }
    public void delVideoFromDownload(PreserveVideo video,boolean isShortVideo){
        if(isShortVideo){
            for(int i=0;i<mShortVideos.size();i++){
                PreserveVideo tmp=mShortVideos.get(i);
                if(tmp.getId().equals(video.getId())){
                    mShortVideos.remove(tmp);
                    Set<String> list=mShortVideoIdHelper.getVideoIdList();
                    list.remove(tmp.getId());
                    mShortVideoIdHelper.setVideoIDs(list);
                    String url=(video.getVideoUrl()!=null&&video.getVideoUrl().size()>0)?video.getVideoUrl().get(0):"";
                    final File dest=new File(url);
                    if(dest.exists()){
                        FixedThreadPool.getInstance().submit(new Runnable() {
                            @Override
                            public void run() {
                                dest.delete();
                            }
                        });

                    }
                    break;
                }
            }

        }else{
            for(int i=0;i<mLandVideos.size();i++){
                PreserveVideo tmp=mLandVideos.get(i);
                if(tmp.getId().equals(video.getId())){
                    mLandVideos.remove(tmp);
                    Set<String> list=mLandVideoIdHelper.getVideoIdList();
                    list.remove(tmp.getId());
                    mLandVideoIdHelper.setVideoIDs(list);
                    String url=(video.getVideoUrl()!=null&&video.getVideoUrl().size()>0)?video.getVideoUrl().get(0):"";
                    final File dest=new File(url);
                    if(dest.exists()){
                        FixedThreadPool.getInstance().submit(new Runnable() {
                            @Override
                            public void run() {
                                dest.delete();
                            }
                        });
                    }
                    break;
                }
            }
        }
        mVideoCoverHelper.remove(video.getId());
        mVideoPathHelper.remove(video.getId());
        mVideoNameHelper.remove(video.getId());
        mVideoDescHelper.remove(video.getId());
    }
}
