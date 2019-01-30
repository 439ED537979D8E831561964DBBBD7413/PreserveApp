package com.social.preserve.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.dkplayer.bean.VideoModel;
import com.dueeeke.dkplayer.widget.videoview.ListIjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.activity.LandscapeVideoDetailActivity;
import com.social.preserve.ui.activity.MainActivity;
import com.social.preserve.ui.activity.VideoReviewActivity;
import com.social.preserve.ui.views.VideoMoreOpeWindow;
import com.social.preserve.utils.ImageTools2;
import com.social.preserve.utils.StringUtil;
import com.social.preserve.utils.TalkingDataKeyEvent;
import com.tendcloud.tenddata.TCAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_CENTER_CROP;
import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_MATCH_PARENT;

/**
 * Created by pt198 on 20/09/2018.
 */

public class LandscapeVideoAdapter extends RecyclerView.Adapter<LandscapeVideoAdapter.ViewHolder> {

    private Context mContext;

    public List<PreserveVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<PreserveVideo> mVideos) {
        this.mVideos.clear();
        this.mVideos.addAll(mVideos);
        mUrlRetryIndexMap.clear();
        for(PreserveVideo video:mVideos){
            mUrlRetryIndexMap.put(video.getId(),0);
        }
        notifyDataSetChanged();
    }

    private List<PreserveVideo> mVideos = new ArrayList<PreserveVideo>();
    private LayoutInflater mInflater;
    private static final String TAG = "LandscapeVideoAdapter";
    public LandscapeVideoAdapter(Activity context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);

    }

    public void clear() {
        if (mVideos.size() > 0) {
            notifyItemRangeRemoved(0, mVideos.size());
            mVideos.clear();
        }
    }


    public void addDatas(List<PreserveVideo> datas) {
        if(null!=datas && datas.size()>0){

            int currPosition = 0;
            if(mVideos.size()>0){
                currPosition = mVideos.size()-1;
            }
            this.mVideos.addAll(datas);
            if(currPosition==0){
                notifyDataSetChanged();
            }else{
                notifyItemRangeChanged(currPosition,mVideos.size());
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.layout_landscape_video_item, null));
    }
    private Map<String,Integer> mUrlRetryIndexMap=new HashMap<>();


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PreserveVideo video = mVideos.get(position) ;
        if (null==holder.ivVideo.getTag(R.id.iv_video) || !video.getCover().equals(holder.ivVideo.getTag(R.id.iv_video))) {
            holder.ivVideo.setTag(R.id.iv_video,video.getCover());
            ImageTools2.show800(holder.controller.getThumb(),video.getCover() );
        }

//        int length = video.getName().length() ;
//        if(length > 8){
//            holder.tvName.setText(video.getName().substring(0,8)+"...") ;
//        }else {
//
//            holder.tvName.setText(video.getName());
//        }
        holder.videoName.setText(video.getPublisher());
        holder.contentTv.setText(video.getDescribed());
        holder.tagsTv.setText(StringUtil.convertToLabels(video.getLabel()));
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,LandscapeVideoDetailActivity.class) ;
                long seek=0;
                if(holder.ijkVideoView.isPlaying()) {
                    seek=holder.ijkVideoView.getCurrentPosition();
                    VideoViewManager.instance().stopPlayback();
                    VideoViewManager.instance().releaseVideoPlayer();
                }
                intent.putExtra("seek", seek) ;
                intent.putExtra("video", (Serializable) video) ;
                mContext.startActivity(intent);

            }
        });
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "ivMore onClick: ");
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.SHOW_LAND_VIDEO_MORE_OPE_MENU);
                int index=mUrlRetryIndexMap.get(video.getId());
                VideoMoreOpeWindow.show((Activity)mContext,video, holder.ivMore,index);
            }
        });
        holder.ijkVideoView.setPlayerConfig(holder.mPlayerConfig);
        String url=(video.getVideoUrl()!=null&&video.getVideoUrl().size()>0)?video.getVideoUrl().get(0):"";
        holder.ijkVideoView.setUrl(url);
//        holder.ijkVideoView.setTitle(video.getName());
        holder.ijkVideoView.setVideoController(holder.controller);
        holder.ijkVideoView.setPlayListener(new IjkVideoView.OnPlayListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onPrepared() {

            }

            @Override
            public void onError() {
                int index=mUrlRetryIndexMap.get(video.getId());
                index++;

                if(video.getVideoUrl()!=null&&index<video.getVideoUrl().size()){
                    String url=video.getVideoUrl().get(index);
                    holder.ijkVideoView.setUrl(url);
                    holder.ijkVideoView.start();
                }else{
                    index=0;
                }
                mUrlRetryIndexMap.put(video.getId(),index);
            }

            @Override
            public void onInfo(int what, int extra) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mVideos.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rl_content)
        View content;
        @BindView(R.id.iv_video)
        ImageView ivVideo;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_review_num)
        TextView tvReviewNum;

        @BindView(R.id.iv_nation_flag)
        ImageView ivNationFlag;
        @BindView(R.id.tv_country)
        TextView tvCountryName;

        @BindView(R.id.ll_nation)
        LinearLayout mLLNation;
        @BindView(R.id.tv_video_name)
        TextView videoName;
        @BindView(R.id.video_player)
        IjkVideoView ijkVideoView;
        @BindView(R.id.tvContent)
        TextView contentTv;
        @BindView(R.id.tvTags)
        TextView tagsTv;
        @BindView(R.id.iv_more)
        ImageView ivMore;

        private StandardVideoController controller;
        private PlayerConfig mPlayerConfig;
        private int currentUrlIndex;
        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
            int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new RelativeLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(mContext);
            mPlayerConfig = new PlayerConfig.Builder()
                    .enableCache()
//                    .autoRotate()
                    .addToPlayerManager()//required
//                        .savingProgress()
                    .build();
        }
    }
}
