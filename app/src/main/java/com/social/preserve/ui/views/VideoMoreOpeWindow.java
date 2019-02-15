package com.social.preserve.ui.views;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.activity.VideoReviewActivity;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.ScreenUtils;
import com.social.preserve.utils.ShareUtils;
import com.social.preserve.utils.TalkingDataKeyEvent;
import com.tendcloud.tenddata.TCAgent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * Created by pt198 on 05/11/2018.
 */

public class VideoMoreOpeWindow {

    public static void show(final Activity activity,final PreserveVideo video,View parent,final int currentUrlIndex){
        final PopupWindow pop = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View content = LayoutInflater.from(activity).inflate(R.layout.layout_more_ope, null);
        View addFav = content.findViewById(R.id.ll_add_fav);
        View download=content.findViewById(R.id.ll_download);
        View share = content.findViewById(R.id.ll_share);
        final ImageView ivAddFav=content.findViewById(R.id.iv_add_fav);
        addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pop.dismiss();
                boolean isFav=false;
                for(int i = 0; i< VideoManager.getInstace().getLandFavVideos().size(); i++){
                    if(VideoManager.getInstace().getLandFavVideos().get(i).getId().equals(video.getId())){
                        isFav=true;
                        break;
                    }
                }
                if(isFav){
                    Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.del_from_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().delVideoFromFav(video,false);
                    ivAddFav.setImageResource(R.mipmap.icon_unfav);

                }else{
                    TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.ADD_FAV_LAND_VIDEO);
                    Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.add_to_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().addVideoToFav(video,false);
                    ivAddFav.setImageResource(R.mipmap.icon_fav);
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.DOWNLOAD_LAND_VIDEO);
                pop.dismiss();
                Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.add_to_download_list), Toast.LENGTH_SHORT).show();
                String url=(video.getVideoUrl()!=null&&video.getVideoUrl().size()>currentUrlIndex)?video.getVideoUrl().get(currentUrlIndex):"";
                DownloadManager.getInstace().submitDownloadVideoTask(video.getId(),url,System.currentTimeMillis()+".mp4",video.getCover(),false);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.SHARE_LAND_VIDEO);
                pop.dismiss();
//                String url=(video.getVideoUrl()!=null&&video.getVideoUrl().size()>0)?video.getVideoUrl().get(0):"";
                String shareUrl= Api.LANDVIDEO_SHARE_URL+video.getId();
                ShareUtils.shareFaceBook(activity, "", "", shareUrl, new PlatformActionListener(){

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                        Toast.makeText(App.getInstance(), "share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                        Toast.makeText(App.getInstance(), "share fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                        Toast.makeText(App.getInstance(), "share cancel", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        boolean isFav=false;
        for(int i = 0; i< VideoManager.getInstace().getLandFavVideos().size(); i++){
            if(VideoManager.getInstace().getLandFavVideos().get(i).getId().equals(video.getId())){
                isFav=true;
                break;
            }
        }
        if(isFav){
            ivAddFav.setImageResource(R.mipmap.icon_fav);
        }else{
            ivAddFav.setImageResource(R.mipmap.ic_video_fav_gray);
        }
        pop.setContentView(content);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x34000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        pop.setBackgroundDrawable(dw);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.3f; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1.0f; //0.0-1.0
                activity.getWindow().setAttributes(lp);
            }
        });
        pop.showAtLocation(parent,Gravity.BOTTOM,0,0);
    }
}
