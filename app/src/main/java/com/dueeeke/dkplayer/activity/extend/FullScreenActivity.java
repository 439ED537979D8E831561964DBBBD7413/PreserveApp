package com.dueeeke.dkplayer.activity.extend;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dueeeke.dkplayer.widget.controller.FullScreenController;
import com.dueeeke.dkplayer.widget.videoview.FullScreenIjkVideoView;
import com.dueeeke.dkplayer.widget.videoview.ListIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.social.preserve.R;
import com.social.preserve.model.PreserveVideo;

import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_CENTER_CROP;
import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_MATCH_PARENT;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity{

    private FullScreenIjkVideoView ijkVideoView;
    private PreserveVideo mVideo;
    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_fullscreen_directly);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mHandler=new Handler();
        ijkVideoView = new FullScreenIjkVideoView(this);
        setContentView(ijkVideoView);
        mVideo=(PreserveVideo) getIntent().getSerializableExtra("video");
        PlayerConfig config = new PlayerConfig.Builder().autoRotate().build();
        ijkVideoView.setPlayerConfig(config);
        ijkVideoView.setTitle(mVideo.getPublisher());
        String url=(mVideo.getVideoUrl()!=null&&mVideo.getVideoUrl().size()>0)?mVideo.getVideoUrl().get(0):"";
        ijkVideoView.setUrl(url);
        ijkVideoView.setVideoController(new FullScreenController(this));
        ijkVideoView.start();
        ijkVideoView.setOnInfoListener(new FullScreenIjkVideoView.OnInfoListener() {

            @Override
            public void onInfo(int what, int extra) {
                if(what== MediaPlayer.MEDIA_INFO_BUFFERING_START){
//                    currentPbLoading.setVisibility(View.VISIBLE);
                }else if(what== MediaPlayer.MEDIA_INFO_BUFFERING_END){
//                    currentPbLoading.setVisibility(View.GONE);
                }else if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                    //第一帧视频已成功渲染

                    int width = ijkVideoView.getWidth();
                    int height = ijkVideoView.getHeight();
                    if(width>height){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ijkVideoView.setScreenScale(SCREEN_SCALE_MATCH_PARENT);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ijkVideoView.setScreenScale(SCREEN_SCALE_CENTER_CROP);
                            }
                        });

                    }
                }else if(what==MediaPlayer.MEDIA_INFO_UNKNOWN){

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }
}
