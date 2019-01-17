package com.social.preserve.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.dueeeke.dkplayer.bean.VideoModel;
import com.dueeeke.dkplayer.widget.videoview.ListIjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.utils.ImageTools2;
import com.social.preserve.utils.ShareUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_CENTER_CROP;
import static com.dueeeke.videoplayer.player.IjkVideoView.SCREEN_SCALE_MATCH_PARENT;


/**
 * Created by pt198 on 20/09/2018.
 */

public class VideoReviewActivity extends BaseActivity {

    private List<PreserveVideo> videoList = new ArrayList<>();

    private static final String TAG = "VideoReviewActivity";

    private RecyclerView mRecyclerView;
    private VideoPreviewAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;


    //是否正在播放
    private boolean isPlaying = true;

    private int mPosition;

    private ListIjkVideoView currentVideoView;
    private ImageView currentIvPlay;
    private ProgressBar currentPro;
    private ImageView currentIvPhoto;
    private ImageView currentIvFollow;
    private TextView currentTvFollowCount;
    private ImageView currentIvZan;
    private ImageView currentIvShare;
    private TextView currentTvZanCount;
    private CircleProgressBar currentPbLoading;
    private ImageView currentIvCover;
    private PreserveVideo currentVideo;
    private int followStatus = 0;
    private int zanStatus = 0;
    private ImageView currentIvVideo;
    private ImageView currentIvVoice;
    private ImageView currentIvGift;
    private View mCurrentLockLayout;
    boolean isFollowing = false;
    boolean isZaning = false;
    SharedPreferences mSp;
    private static final String SP_KEY="short_video_guide";
    private static final String GUIDE_KEY="guide";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initData() {
        videoList = (List<PreserveVideo>) getIntent().getSerializableExtra("list");
        mPosition = getIntent().getIntExtra("position", 0);
        mAdapter.addDatas(videoList);
        currentVideo = videoList.get(mPosition) ;
        mRecyclerView.scrollToPosition(mPosition);

    }


    private void initView() {
        showCommonTopBar(View.GONE);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoReviewActivity.this.finish();
            }
        });


        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new VideoPreviewAdapter(VideoReviewActivity.this);
        mAdapter.clearDatas();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_review;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }






    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {
                Log.d(TAG, "onInitComplete");
                toPlayVideo();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.d(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.d(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                mPosition = position;
                currentVideo = videoList.get(position) ;
                toPlayVideo();
                boolean isFav=false;
                for(int i=0;i< VideoManager.getInstace().getShortFavVideos().size();i++){
                    if(VideoManager.getInstace().getShortFavVideos().get(i).getId().equals(currentVideo.getId())){
                        isFav=true;
                        break;
                    }
                }
                if(isFav){

                    currentIvFollow.setImageResource(R.mipmap.icon_fav);

                }else{

                    currentIvFollow.setImageResource(R.mipmap.icon_unfav);
                }
            }


        });

    }

    private void toPlayVideo() {

        View itemView = mRecyclerView.getChildAt(0);
        final ListIjkVideoView videoView = itemView.findViewById(R.id.videoView);
        final ImageView ivPlay = itemView.findViewById(R.id.ivPlay);
        final ImageView ivCover = itemView.findViewById(R.id.ivCover);
        final CircleProgressBar pbLoading = itemView.findViewById(R.id.pbLoading);
        final ProgressBar progressBar = itemView.findViewById(R.id.progressBar);
        final ImageView ivPhoto = itemView.findViewById(R.id.ivPhoto);
        final ImageView ivFollow = itemView.findViewById(R.id.ivFoolow);
        final TextView tvFollowCount = itemView.findViewById(R.id.tvFollowCount);
        final ImageView ivZan = itemView.findViewById(R.id.ivZan);
        final TextView tvZanCount = itemView.findViewById(R.id.tvZanCount);
        final ImageView ivVideo = itemView.findViewById(R.id.ivVideo);
        final ImageView ivVoice = itemView.findViewById(R.id.ivVoice);
        final ImageView ivGift = itemView.findViewById(R.id.ivGift);
        final ImageView ivShare = itemView.findViewById(R.id.ivShare);

        currentVideoView = videoView;
        currentIvPlay = ivPlay;
        currentPro = progressBar;
        currentIvPhoto = ivPhoto;
        currentIvFollow = ivFollow;
        currentTvFollowCount = tvFollowCount;
        currentIvZan = ivZan;
        currentTvZanCount = tvZanCount;
        currentPbLoading = pbLoading;
        currentIvCover = ivCover;
        currentIvVideo = ivVideo;
        currentIvVoice = ivVoice;
        currentIvGift = ivGift;
        currentIvShare = ivShare;
        mCurrentLockLayout=itemView.findViewById(R.id.ll_locked_state);

        playVideo();
        initViewAndInfo();


    }


    private void initViewAndInfo() {
        if (currentVideo == null) {
            Toast.makeText(VideoReviewActivity.this,getString(R.string.load_data_fail),Toast.LENGTH_SHORT).show();
            return;
        }
//
//        if(null!=App.getLoginUser()){
//            followStatus(currentVideo.getTargetId() + "");
//            fansCount(currentVideo.getTargetId() + "");
//            updatePlayCount(currentVideo.getId() + "");
//        }

        currentIvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!checkVisitorPermission()){
//                    return;
//                }
//                toFollow(currentVideo.getTargetId() + "");
                boolean isFav=false;
                for(int i=0;i< VideoManager.getInstace().getShortFavVideos().size();i++){
                    if(VideoManager.getInstace().getShortFavVideos().get(i).getId().equals(currentVideo.getId())){
                        isFav=true;
                        break;
                    }
                }
                if(isFav){
                    Toast.makeText(App.getInstance(), getString(R.string.del_from_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().delVideoFromFav(currentVideo,true);
                    currentIvFollow.setImageResource(R.mipmap.icon_unfav);

                }else{
                    Toast.makeText(App.getInstance(), getString(R.string.add_to_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().addVideoToFav(currentVideo,true);
                    currentIvFollow.setImageResource(R.mipmap.icon_fav);
                }
            }
        });
        currentIvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        currentIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        currentIvVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        currentIvGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        currentIvZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(App.getInstance(), getString(R.string.add_to_download_list), Toast.LENGTH_SHORT).show();
                DownloadManager.getInstace().submitDownloadVideoTask(currentVideo.getVideoUrl(),System.currentTimeMillis()+".mp4",currentVideo.getCover(),true);
            }
        });
        currentIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(getString(R.string.loading));
                String url=currentVideo.getVideoUrl();
                ShareUtils.shareFaceBook(VideoReviewActivity.this, "", "", url, new PlatformActionListener(){

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        if(isFinishing()||isDestroyed()){
                            return;
                        }
                        dissLoad();
                        Toast.makeText(VideoReviewActivity.this, "share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        if(isFinishing()||isDestroyed()){
                            return;
                        }
                        dissLoad();
                        Toast.makeText(VideoReviewActivity.this, "share fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        if(isFinishing()||isDestroyed()){
                            return;
                        }
                        dissLoad();
                        Toast.makeText(VideoReviewActivity.this, "share cancel", Toast.LENGTH_SHORT).show();
                    }
                });
//                CommonHelper.showSharePopWindow(VideoReviewActivity.this, new ShareWindow.SelectShareListener() {
//                    @Override
//                    public void select(String platForm) {
//                        super.select(platForm);
//
//                        if(platForm.equals("1")){
//                            CommonHelper.shareFaceBook(VideoReviewActivity.this, platForm,"share Video", "",  currentVideo.getId()+"" ,new ApiCallback<Boolean>(){
//
//                                @Override
//                                public void onSuccess(Boolean data) {
//                                    reportUserTask();
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toasty.info(mContext, "share successful ", 200,false).show();
//
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onError(final String error) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toasty.error(mContext, error, 200,false).show();
//                                        }
//                                    });
//
//                                }
//                            });
//                            return ;
//                        }
//                        String title = currentVideo.getNickName() + " " + getString(R.string.release_one_video);
//                        CommonHelper.doShare(VideoReviewActivity.this, platForm, title, currentVideo.getContent(), currentVideo.getUrl(), currentVideo.getPhoto(), new ApiCallback<Boolean>() {
//                            @Override
//                            public void onSuccess(Boolean data) {
//                                reportUserTask();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toasty.info(mContext, "share successful ", 200,false).show();
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onError(final String error) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toasty.error(mContext, error, 200,false).show();
//                                    }
//                                });
//
//                            }
//                        });
//                    }
//                });
            }
        });
        mCurrentLockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    /**
     * 播放视频
     */
    private void playVideo() {
        currentVideoView.setOnPlayListener(new ListIjkVideoView.OnPlayListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onPrepared() {
                currentPbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                currentPbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onInfo(int what, int extra) {
                if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){
//                    currentPbLoading.setVisibility(View.VISIBLE);
                }else if(what== MediaPlayer.MEDIA_INFO_BUFFERING_END){
//                    currentPbLoading.setVisibility(View.GONE);
                }else if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                    //第一帧视频已成功渲染
                    currentIvCover.animate().alpha(0).setDuration(1).start();
                    int width = currentVideoView.getWidth();
                    int height = currentVideoView.getHeight();
                    if(width>height){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentVideoView.setScreenScale(SCREEN_SCALE_MATCH_PARENT);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentVideoView.setScreenScale(SCREEN_SCALE_CENTER_CROP);
                            }
                        });

                    }
                    mHandler.post(runnable);
                }else if(what==MediaPlayer.MEDIA_INFO_UNKNOWN){
                    currentPbLoading.setVisibility(View.GONE);
                }
            }
        });
//        currentVideoView.setOnErrorListener(new PLOnErrorListener() {
//            @Override
//            public boolean onError(int i) {
//                String tip = "play error";
//
//                switch (i) {
//                    case MEDIA_ERROR_UNKNOWN:
//                        //未知错误
//                        currentPbLoading.setVisibility(View.GONE);
//                        tip = "video info error";
//                        break;
//                    case ERROR_CODE_OPEN_FAILED:
//                        //播放器打开失败
//                        currentPbLoading.setVisibility(View.GONE);
//                        tip = "video info error";
//                        break;
//                    case ERROR_CODE_IO_ERROR:
//                        //网络异常
//                        currentPbLoading.setVisibility(View.GONE);
//                        tip = "network error";
//                        break;
//                    case ERROR_CODE_SEEK_FAILED:
//                        //拖动失败
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//                    case ERROR_CODE_CACHE_FAILED:
//                        //预加载失败
//                        currentPbLoading.setVisibility(View.GONE);
//
//                        break;
//                    case ERROR_CODE_HW_DECODE_FAILURE:
//                        //硬解失败
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//                    case ERROR_CODE_PLAYER_DESTROYED:
//                        //播放器已被销毁，需要再次 setVideoURL 或 prepareAsync
//                        currentPbLoading.setVisibility(View.GONE);
//                        tip = "video player has destroy";
//                        break;
//                    case ERROR_CODE_PLAYER_VERSION_NOT_MATCH:
//                        //so 库版本不匹配，需要升级
//                        currentPbLoading.setVisibility(View.GONE);
//                        tip = "player version  error";
//                        break;
//                    case ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED:
//                        //AudioTrack 初始化失败，可能无法播放音频
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//                }
//
//                return false;
//            }
//
//        });
//        currentVideoView.setOnInfoListener(new PLOnInfoListener() {
//            @Override
//            public void onInfo(int i, int extra) {
//                switch (i) {
//                    case MEDIA_INFO_VIDEO_RENDERING_START:
//                        //第一帧视频已成功渲染
//                        currentIvCover.animate().alpha(0).setDuration(1).start();
//                        int width = currentVideoView.getWidth();
//                        int height = currentVideoView.getHeight();
//                        if(width>height){
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    currentVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
//                                }
//                            });
//                        }else{
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    currentVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
//                                }
//                            });
//
//                        }
//                        mHandler.post(runnable);
//                        break;
//                    case MEDIA_INFO_CONNECTED:
//                        //连接成功
//
//                        break;
//                    case MEDIA_INFO_BUFFERING_START:
//                        Log.d(TAG, "MEDIA_INFO_BUFFERING_START onInfo: "+extra);
//                        //开始缓冲
//                        currentPbLoading.setVisibility(View.VISIBLE);
//                        break;
//                    case MEDIA_INFO_BUFFERING_END:
//                        //停止缓冲
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//                    case MEDIA_INFO_CACHE_DOWN:
//                        //预加载完成
//                        break;
//                    case MEDIA_INFO_UNKNOWN:
//                        //未知消息
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//                    case MEDIA_INFO_SWITCHING_SW_DECODE:
//                        //硬解失败，自动切换软解
//                        currentPbLoading.setVisibility(View.GONE);
//                        break;
//
//
//                }
//
//            }
//
//        });
//
//        currentVideoView.setOnPreparedListener(new PLOnPreparedListener() {
//            @Override
//            public void onPrepared(int i) {
//                Log.i(TAG, "onPrepared: ");
//            }
//
//        });

        currentVideoView.start();
        isPlaying = true;
        currentIvPlay.setVisibility(View.GONE);

        currentIvPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    currentVideoView.pause();
                    isPlaying = false;
                    currentIvPlay.setVisibility(View.VISIBLE);
                } else {
                    currentVideoView.start();
                    isPlaying = true;
                    currentIvPlay.setVisibility(View.GONE);
                }
            }
        });
        currentIvCover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    currentVideoView.pause();
                    isPlaying = false;
                    currentIvPlay.setVisibility(View.VISIBLE);
                } else {
                    currentVideoView.start();
                    isPlaying = true;
                    currentIvPlay.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 更新播放进度
     */
    private void updatePlayProcess() {
        long total = currentVideoView.getDuration();
        long current = currentVideoView.getCurrentPosition();
        currentPro.setMax((int) total);
        if (current > 0) {
            currentPro.setProgress((int) current);
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updatePlayProcess();
            mHandler.postDelayed(runnable, 20);
        }
    };

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            updatePlayProcess();
        }
    };


    /**
     * 销毁释放当前播放的视频
     *
     * @param index
     */
    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        if(itemView==null){
            return;
        }
        final ListIjkVideoView videoView = itemView.findViewById(R.id.videoView);
        final ImageView imgThumb = itemView.findViewById(R.id.ivCover);
        final ImageView imgPlay = itemView.findViewById(R.id.ivPlay);
        videoView.stopPlayback();
        imgThumb.animate().alpha(1).start();
        imgPlay.animate().alpha(0f).start();
    }

    private void destroy() {
        if (null != currentVideoView) {
            currentVideoView.stopPlayback();
        } else {
            finish();
        }
    }

    private void pause() {
        if (null != currentVideoView) {
            currentVideoView.pause();
            isPlaying = false;
            currentIvPlay.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
        mHandler.removeCallbacks(runnable) ;
        mHandler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }
    

    /**
     * 小视频adapter
     */
    class VideoPreviewAdapter extends RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder> {

        public List<PreserveVideo> getVideoList() {
            return videoList;
        }

        private List<PreserveVideo> videoList = new ArrayList<PreserveVideo>();

        private Activity activity;

        public VideoPreviewAdapter(VideoReviewActivity videoReviewActivity) {
            this.activity = videoReviewActivity;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final PreserveVideo video = videoList.get(position);
          //  currentVideo = video;

           /* holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sex = "";
                    if (null != App.getLoginUser() && !App.getLoginUser().getuId().equals(video.getTargetId())) {
                        if (App.getLoginUser().getSex().equals("0")) {
                            sex = "1";
                        } else {
                            sex = "0";
                        }
                    }
                    Log.e("liujw","####################setOnClickListener getTargetId : "+video.getTargetId());
                    jumpActivityExtra(SpaceActivity.class, video.getTargetId() + "", sex);
                }
            });
*/

            ImageTools2.show800(holder.ivCover, video.getCover());
            ImageTools2.showAvatar(holder.ivPhoto, video.getCover());


//            holder.tvContent.setText(video.getName());
            if(!TextUtils.isEmpty(video.getTitle()) && video.getTitle().length()>11){
                holder.tvTitle.setText(video.getTitle().substring(0,10)+"...");
            }else{
                holder.tvTitle.setText(video.getTitle());
            }

//
//            if(!TextUtils.isEmpty(video.getNationalFlag())){
//                ImageTools.show200(mContext,video.getNationalFlag(),holder.ivNationFlag);
//            }
//            if(StringUtils.isNotNull(video.getCountry()) && video.getCountry().length()>14){
//                holder.tvCountryName.setText(video.getCountry().substring(0,13)+"...");
//            }else{
//                holder.tvCountryName.setText(video.getCountry());
//            }
            boolean isFav=false;
            for(int i=0;i< VideoManager.getInstace().getShortFavVideos().size();i++){
                if(VideoManager.getInstace().getShortFavVideos().get(i).getId().equals(video.getId())){
                    isFav=true;
                    break;
                }
            }
            if (isFav) {
                holder.ivFoolow.setImageResource(R.mipmap.icon_fav);

            } else {
                holder.ivFoolow.setImageResource(R.mipmap.icon_unfav);

            }
            holder.tvTags.setText(video.getLabel());
            holder.tvContent.setText(video.getDescribed());
//            zanStatus = video.getZanStatus();

//            holder.tvZanCount.setText(video.getLikeNum() + "");
            PlayerConfig playerConfig = new PlayerConfig.Builder()
                    .enableCache()
                    .setLooping()
                    .addToPlayerManager()//required
//                        .savingProgress()
                    .build();
            holder.videoView.setPlayerConfig(playerConfig);
            boolean isLock=false;
//            if(!"1".equals(App.getVipStatus())&&(App.getLoginUser()!=null&&"1".equals(App.getLoginUser().getSex()))){
//                if(App.getVideoplayCount() < App.MAX_REVIEW_VIDEO_NUM){
//                    isLock=false;
//                }else{
//                    if(!App.getVideoIds().contains(String.valueOf(videoList.get(position).getId()))) {
//                        isLock=true;
//                    }else{
//                        isLock=false;
//                    }
//                }
//            }else{
//                isLock=false;
//            }
//            if(isLock){
//                //视频锁住状态不加载
//                holder.lockedStateLayout.setVisibility(View.VISIBLE);
//                holder.ivPlay.setVisibility(View.GONE);
//                holder.pbLoading.setVisibility(View.GONE);
//            }else{
            holder.pbLoading.setVisibility(View.VISIBLE);
                holder.lockedStateLayout.setVisibility(View.GONE);
                holder.ivPlay.setVisibility(View.VISIBLE);
//                holder.pbLoading.setVisibility(View.VISIBLE);
                List<VideoModel> videoList = new ArrayList<>();
                videoList.add(new VideoModel(video.getVideoUrl(),"",new StandardVideoController(VideoReviewActivity.this), false));
                holder.videoView.setVideos(videoList);
//            }
//
//            if (null != App.getLoginUser() && !App.getLoginUser().getuId().equals(video.getTargetId() + "")) {
//                holder.llBottom.setVisibility(View.VISIBLE);
//                if (App.getLoginUser().getSex().equals("0")) {
//                    holder.rlGift.setVisibility(View.GONE);
//                    holder.ivFoolow.setVisibility(View.GONE);
//                    holder.tvFoolowCount.setVisibility(View.GONE);
//                } else {
//                    holder.rlGift.setVisibility(View.VISIBLE);
//                    holder.ivFoolow.setVisibility(View.VISIBLE);
//                    holder.tvFoolowCount.setVisibility(View.VISIBLE);
//                }
//            } else {
//                holder.llBottom.setVisibility(View.GONE);
//                holder.ivFoolow.setVisibility(View.GONE);
//                holder.tvFoolowCount.setVisibility(View.GONE);
//            }


        }


        @Override
        public int getItemCount() {
            return videoList == null ? 0 : videoList.size();
        }

        public void addDatas(List<PreserveVideo> videoList) {
            this.videoList.addAll(videoList);
            notifyDataSetChanged();
        }

        public void clearDatas() {
            this.videoList.clear();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivCover;
            ImageView ivFoolow;
            TextView tvFoolowCount;
            TextView tvTags;
            ImageView ivZan;
            TextView tvZanCount;
            TextView tvContent;
            TextView tvTitle;
            ImageView ivPhoto;
            ImageView ivVideo;
            ImageView ivVoice;
            ImageView ivGift;

            TextView tvCountryName;
            ImageView ivNationFlag ;

            ListIjkVideoView videoView;
            CircleProgressBar pbLoading;
            ImageView ivPlay;
            ImageView ivShare;
            ProgressBar progressBar;
            LinearLayout llBottom;
            RelativeLayout rlGift;
            LinearLayout lockedStateLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                tvCountryName = itemView.findViewById(R.id.tv_country) ;
                ivNationFlag = itemView.findViewById(R.id.iv_nation_flag) ;
                videoView = itemView.findViewById(R.id.videoView);
                ivCover = itemView.findViewById(R.id.ivCover);
                ivPlay = itemView.findViewById(R.id.ivPlay);
                ivPhoto = itemView.findViewById(R.id.ivPhoto);
                ivFoolow = itemView.findViewById(R.id.ivFoolow);
                tvFoolowCount = itemView.findViewById(R.id.tvFollowCount);
                tvTags=itemView.findViewById(R.id.tvTags);
                ivZan = itemView.findViewById(R.id.ivZan);
                tvZanCount = itemView.findViewById(R.id.tvZanCount);
                progressBar = itemView.findViewById(R.id.progressBar);
                ivShare = itemView.findViewById(R.id.ivShare);
                tvContent = itemView.findViewById(R.id.tvContent);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                pbLoading = itemView.findViewById(R.id.pbLoading);
                ivVideo = itemView.findViewById(R.id.ivVideo);
                ivVoice = itemView.findViewById(R.id.ivVoice);
                ivGift = itemView.findViewById(R.id.ivGift);
                llBottom = itemView.findViewById(R.id.ll_bottom);
                rlGift = itemView.findViewById(R.id.rlGift);
                lockedStateLayout= itemView.findViewById(R.id.ll_locked_state);
            }
        }
    }
}
