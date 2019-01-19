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

import com.dueeeke.dkplayer.activity.extend.FullScreenActivity;
import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.VideoManager;
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

public class DownloadVideoAdapter extends RecyclerView.Adapter<DownloadVideoAdapter.ViewHolder> {

    private Context mContext;

    public List<PreserveVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<PreserveVideo> mVideos) {
        this.mVideos.clear();
        this.mVideos.addAll(mVideos);
        this.mCheckFlag.clear();
        for(int i=0;i<mVideos.size();i++){
            mCheckFlag.add(false);
        }
        notifyDataSetChanged();
    }
    public boolean isEdit;

    public void setEdit(boolean edit) {
        isEdit = edit;
        if(!isEdit){
            cancelAll();
        }else{
            notifyDataSetChanged();
        }
    }

    public void cancelAll(){
        this.mCheckFlag.clear();
        for(int i=0;i<mVideos.size();i++){
            mCheckFlag.add(false);
        }
        notifyDataSetChanged();
    }
    public void selAll(){
        this.mCheckFlag.clear();
        for(int i=0;i<mVideos.size();i++){
            mCheckFlag.add(true);
        }
        notifyDataSetChanged();
    }
    private List<PreserveVideo> mVideos = new ArrayList<PreserveVideo>();
    private List<Boolean> mCheckFlag=new ArrayList<>();
    private LayoutInflater mInflater;
    private boolean isShortVideo;
    private static final String TAG = "ShortVideoAdapter";
    public DownloadVideoAdapter(Context context,boolean isShortVideo) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.isShortVideo=isShortVideo;
    }

    public void clear() {
        if (mVideos.size() > 0) {
            notifyItemRangeRemoved(0, mVideos.size());
            mVideos.clear();
        }
    }

    public void delSelectedItems(){
        for(int i=0;i<mCheckFlag.size();i++){
            if(mCheckFlag.get(i)){
                PreserveVideo dest=mVideos.get(i);
                VideoManager.getInstace().delVideoFromDownload(dest,isShortVideo);
                mCheckFlag.remove(i);
                mVideos.remove(i);
            }
        }
        notifyDataSetChanged();
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

    public List<Boolean> getCheckFlag() {
        return mCheckFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.layout_download_video_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PreserveVideo video = mVideos.get(position) ;
        if (null==holder.ivVideo.getTag(R.id.iv_video) || !video.getCover().equals(holder.ivVideo.getTag(R.id.iv_video))) {
            holder.ivVideo.setTag(R.id.iv_video,video.getCover());
            ImageTools2.show400(holder.ivVideo,video.getCover() );
        }
        if(video.getTitle()!=null) {
            int length = video.getTitle().length();
            if (length > 8) {
                holder.tvName.setText(video.getTitle().substring(0, 8) + "...");
            } else {

                holder.tvName.setText(video.getTitle());
            }
        }else{
            holder.tvName.setText("");
        }
        boolean flag=mCheckFlag.get(position);
        if(isEdit){
            holder.selIv.setVisibility(View.VISIBLE);
        }else{
            holder.selIv.setVisibility(View.GONE);
        }
        if(flag){
            holder.selIv.setImageResource(R.mipmap.check_on);
        }else{
            holder.selIv.setImageResource(R.mipmap.check_off);
        }
        holder.selIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=mCheckFlag.get(position);
                if(flag){
                    mCheckFlag.set(position,false);
                }else{
                    mCheckFlag.set(position,true);
                }
                notifyDataSetChanged();
            }
        });
        if(isShortVideo){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.content.getLayoutParams();
            params.height = (int) (App.screenWidth/3*1.5);
            holder.content.setLayoutParams(params);
        }else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            params.height = (int) (App.screenWidth / 2 * 0.75);
            holder.content.setLayoutParams(params);
        }
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,FullScreenActivity.class) ;
                intent.putExtra("video", (Serializable) video) ;
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
        @BindView(R.id.iv_sel)
        ImageView selIv;
        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
