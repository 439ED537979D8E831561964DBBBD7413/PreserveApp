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
        Set<String> ids=mShortVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: ids "+ids.toString());
        for(String id:ids){
            String cover=mVideoCoverHelper.getValue(id);
            String path=mVideoPathHelper.getValue(id);
            String name=mVideoNameHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            video.setVideoUrl(path);
            video.setCover(cover);
            video.setPublisher(name);
            Log.d(TAG, "mShortVideos: id "+id+",path "+path+",cover "+cover);
            mShortVideos.add(video);
        }
        Set<String> landIds=mLandVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: landIds "+landIds.toString());
        for(String id:landIds){
            String cover=mVideoCoverHelper.getValue(id);
            String path=mVideoPathHelper.getValue(id);
            String name=mVideoNameHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            video.setVideoUrl(path);
            video.setCover(cover);
            video.setPublisher(name);
            Log.d(TAG, "mLandVideos: id "+id+",path "+path+",cover "+cover);
            mLandVideos.add(video);
        }


        Set<String> favids=mShortFavVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: favids= "+favids.toString());
        for(String id:favids){
            String cover=mVideoFavCoverHelper.getValue(id);
            String path=mVideoFavPathHelper.getValue(id);
            String name=mVideoFavNameHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            video.setVideoUrl(path);
            video.setCover(cover);
            video.setPublisher(name);
            Log.d(TAG, "mShortFavVideos: id "+id+",path "+path+",cover "+cover);
            mShortFavVideos.add(video);
        }
        Set<String> landFavIds=mLandFavVideoIdHelper.getVideoIdList();
        Log.d(TAG, "initData: landFavIds= "+landFavIds.toString());
        for(String id:landFavIds){
            String cover=mVideoFavCoverHelper.getValue(id);
            String path=mVideoFavPathHelper.getValue(id);
            String name=mVideoFavNameHelper.getValue(id);
            PreserveVideo video=new PreserveVideo();
            video.setVideoUrl(path);
            video.setCover(cover);
            video.setPublisher(name);
            Log.d(TAG, "mLandFavVideos: id "+id+",path "+path+",cover "+cover);
            mLandFavVideos.add(video);
        }
    }
    public void saveVideoInfoToDb(PreserveVideo video,boolean isShortVideo){
        mVideoCoverHelper.setValue(video.getId(),video.getCover());
        mVideoPathHelper.setValue(video.getId(),video.getVideoUrl());
        mVideoNameHelper.setValue(video.getId(),video.getPublisher());
        if(isShortVideo) {
            mShortVideoIdHelper.setVideoIDs(video.getId());
            mShortVideos.add(video);
        }else{
            mLandVideoIdHelper.setVideoIDs(video.getId());
            mLandVideos.add(video);
        }
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
        mVideoFavPathHelper.setValue(video.getId(),video.getVideoUrl());
        mVideoFavNameHelper.setValue(video.getId(),video.getPublisher());
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
                    final File dest=new File(video.getVideoUrl());
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
                    final File dest=new File(video.getVideoUrl());
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
    }
}
