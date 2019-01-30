package com.social.preserve.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.social.preserve.R;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.activity.LandscapeVideoDetailActivity;
import com.social.preserve.utils.ImageTools2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pt198 on 20/09/2018.
 */

public class OtherVideoAdapter extends RecyclerView.Adapter<OtherVideoAdapter.ViewHolder> {

    private Context mContext;

    public List<PreserveVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<PreserveVideo> mVideos) {
        this.mVideos = mVideos;
        notifyDataSetChanged();
    }

    private List<PreserveVideo> mVideos = new ArrayList<PreserveVideo>();
    private LayoutInflater mInflater;
    private static final String TAG = "OtherVideoAdapter";

    public OtherVideoAdapter(Context context) {
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
        if (null != datas && datas.size() > 0) {

            int currPosition = 0;
            if (mVideos.size() > 0) {
                currPosition = mVideos.size() - 1;
            }
            this.mVideos.addAll(datas);
            if (currPosition == 0) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeChanged(currPosition, mVideos.size());
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.layout_other_video_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PreserveVideo video = mVideos.get(position);
        if (null == holder.ivVideo.getTag(R.id.iv_video) || !video.getCover().equals(holder.ivVideo.getTag(R.id.iv_video))) {
            holder.ivVideo.setTag(R.id.iv_video, video.getCover());
            ImageTools2.show400(holder.ivVideo, video.getCover());
        }
        holder.tvTitle.setText(video.getPublisher());
        holder.tagsTv.setText(video.getDescribed());
        holder.rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof LandscapeVideoDetailActivity){
                    ((Activity) mContext).finish();
                }
                Intent intent = new Intent(mContext, LandscapeVideoDetailActivity.class);
                intent.putExtra("video", (Serializable) video);
                mContext.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mVideos.size());
        return mVideos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_video)
        ImageView ivVideo;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rl_content)
        RelativeLayout rlContent;
        @BindView(R.id.tv_tags)
        TextView tagsTv;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
