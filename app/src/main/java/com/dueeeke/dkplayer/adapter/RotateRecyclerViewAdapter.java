package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.social.preserve.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.widget.controller.RotateInFullscreenController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

import java.util.List;

public class RotateRecyclerViewAdapter extends RecyclerView.Adapter<RotateRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;
    private Context context;

    public RotateRecyclerViewAdapter(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_video_rotate, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        VideoBean videoBean = videos.get(position);
        Glide.with(context)
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(holder.controller.getThumb());
        holder.ijkVideoView.setPlayerConfig(holder.mPlayerConfig);
        holder.ijkVideoView.setUrl(videoBean.getUrl());
        holder.ijkVideoView.setTitle(videoBean.getTitle());
        holder.ijkVideoView.setVideoController(holder.controller);
        holder.title.setText(videoBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private IjkVideoView ijkVideoView;
        private RotateInFullscreenController controller;
        private TextView title;
        private PlayerConfig mPlayerConfig;

        VideoHolder(View itemView) {
            super(itemView);
            ijkVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new RotateInFullscreenController(context);
            title = itemView.findViewById(R.id.tv_title);
            mPlayerConfig = new PlayerConfig.Builder()
//                        .enableCache()
//                        .autoRotate()//不能开启自动旋转
                    .addToPlayerManager()//required
//                        .savingProgress()
                    .build();
        }
    }
}