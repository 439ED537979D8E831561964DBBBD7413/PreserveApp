package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.interf.ListMediaPlayerControl;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.dkplayer.bean.VideoModel;

import java.util.List;

/**
 * 连续播放一个列表
 * Created by xinyu on 2017/12/25.
 */

public class ListIjkVideoView extends IjkVideoView implements ListMediaPlayerControl{

    protected List<VideoModel> mVideoModels;//列表播放数据
    protected int mCurrentVideoPosition = 0;//列表播放时当前播放视频的在List中的位置
    private OnPlayListener mListener;
    public void setOnPlayListener( OnPlayListener listener){
        this.mListener=listener;
    }
    public ListIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public ListIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public interface OnPlayListener{
        void onComplete();
        void onPrepared();
        void onError();
        void onInfo(int what, int extra);
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        if(mListener!=null){
            mListener.onComplete();
        }
        skipToNext();
    }

    @Override
    public void onError() {
        super.onError();
        if(mListener!=null){
            mListener.onError();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        if(mListener!=null){
            mListener.onInfo(what,extra);
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if(mListener!=null){
            mListener.onPrepared();
        }
    }

    /**
     * 播放下一条视频
     */
    private void playNext() {
        VideoModel videoModel = mVideoModels.get(mCurrentVideoPosition);
        if (videoModel != null) {
            mCurrentUrl = videoModel.url;
            mCurrentTitle = videoModel.title;
            mPlayerConfig.isCache = videoModel.isCache;
            mCurrentPosition = 0;
            setVideoController(videoModel.controller);
        }
    }

    /**
     * 设置一个列表的视频
     */
    public void setVideos(List<VideoModel> videoModels) {
        this.mVideoModels = videoModels;
        playNext();
    }

    /**
     * 播放下一条视频，可用于跳过广告
     */
    @Override
    public void skipToNext() {
        mCurrentVideoPosition++;
        if (mVideoModels != null && mVideoModels.size() > 1) {
            if (mCurrentVideoPosition >= mVideoModels.size()) return;
            playNext();
            addDisplay();
            startPrepare(true);

        }
    }
}
