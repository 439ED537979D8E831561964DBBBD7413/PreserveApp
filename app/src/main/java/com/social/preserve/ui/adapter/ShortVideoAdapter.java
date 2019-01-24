package com.social.preserve.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.activity.VideoReviewActivity;
import com.social.preserve.utils.ImageTools2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pt198 on 20/09/2018.
 */

public class ShortVideoAdapter extends RecyclerView.Adapter<ShortVideoAdapter.ViewHolder> {

    private Context mContext;

    public List<PreserveVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<PreserveVideo> mVideos) {
        this.mVideos.clear();
        this.mVideos.addAll(mVideos);
        notifyDataSetChanged();
    }

    private List<PreserveVideo> mVideos = new ArrayList<PreserveVideo>();
    private LayoutInflater mInflater;
    private static final String TAG = "ShortVideoAdapter";
    public ShortVideoAdapter(Context context) {
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
        return new ViewHolder(mInflater.inflate(R.layout.layout_video_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PreserveVideo video = mVideos.get(position) ;
        if (null==holder.ivVideo.getTag(R.id.iv_video) || !video.getCover().equals(holder.ivVideo.getTag(R.id.iv_video))) {
            holder.ivVideo.setTag(R.id.iv_video,video.getCover());
            ImageTools2.show400(holder.ivVideo,video.getCover() );
        }
        if(video.getPublisher()!=null) {
            int length = video.getPublisher().length();
            if (length > 8) {
                holder.tvName.setText(video.getPublisher().substring(0, 8) + "...");
            } else {

                holder.tvName.setText(video.getPublisher());
            }
        }else{
            holder.tvName.setText("");
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.content.getLayoutParams();
        params.height = (int) (App.screenWidth/3*1.5);
        holder.content.setLayoutParams(params);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,VideoReviewActivity.class) ;
                intent.putExtra("list", (Serializable) mVideos) ;
                intent.putExtra("position",position) ;
                Log.d(TAG, "onClick: position "+position+","+mVideos.get(position).getVideoUrl()+","+mVideos.get(position).getCover());
                mContext.startActivity(intent);

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
        @BindView(R.id.iv_logo)
        ImageView ivLogo;
        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
