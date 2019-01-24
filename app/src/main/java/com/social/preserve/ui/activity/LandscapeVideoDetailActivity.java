package com.social.preserve.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.dkplayer.bean.VideoModel;
import com.dueeeke.dkplayer.widget.videoview.ListIjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.adapter.OtherVideoAdapter;
import com.social.preserve.ui.views.HotokRefreshLayout;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.ShareUtils;
import com.social.preserve.utils.TalkingDataKeyEvent;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * Created by pt198 on 10/01/2019.
 */

public class LandscapeVideoDetailActivity extends BaseActivity {
    @BindView(R.id.video_player)
    ListIjkVideoView videoPlayer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_like)
    ImageView ivLike;
    @BindView(R.id.iv_download)
    ImageView ivDownload;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.ll_ope)
    LinearLayout llOpe;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.rv_other_videos)
    RecyclerView rvOtherVideos;
    @BindView(R.id.video_detail_RefreshLayout)
    HotokRefreshLayout mRefreshLayout;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvTags)
    TextView tvTags;
    @BindView(R.id.tv_download)
    TextView tvDownload;
    @BindView(R.id.ll_download)
    LinearLayout llDownload;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.ll_share)
    LinearLayout llShare;
    private PreserveVideo mVideo;
    private List<PreserveVideo> mOtherVideos = new ArrayList<>();
    private OtherVideoAdapter mAdapter;
    private int page = 1;
    private boolean mPlayComplete;
    private static final String TAG = "LandscapeVideoDetailAct";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        ButterKnife.bind(this);
        initView();
        loadOtherVideos();
    }

    private void getIntentData() {
        mVideo = (PreserveVideo) getIntent().getSerializableExtra("video");
    }

    private void initView() {
        showCommonTopBar(View.GONE);
        initVideo();
        initRecycleView();
        boolean isFav = false;
        for (int i = 0; i < VideoManager.getInstace().getLandFavVideos().size(); i++) {
            if (VideoManager.getInstace().getLandFavVideos().get(i).getId().equals(mVideo.getId())) {
                isFav = true;
                break;
            }
        }
        if (isFav) {

            ivLike.setImageResource(R.mipmap.icon_fav);

        } else {

            ivLike.setImageResource(R.mipmap.ic_video_fav_gray);
        }
        tvTitle.setText(mVideo.getPublisher());
        tvContent.setText(mVideo.getDescribed());
        tvTags.setText(mVideo.getLabel());
    }

    private void initRecycleView() {
        mAdapter = new OtherVideoAdapter(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvOtherVideos.setHasFixedSize(true);
        rvOtherVideos.setLayoutManager(mLayoutManager);
        rvOtherVideos.setAdapter(mAdapter);
        mRefreshLayout.setEnableAutoLoadMore(true);
        mRefreshLayout.setRefreshListener(new HotokRefreshLayout.OnRefreshLoadMoreListener() {
            @Override
            public void refresh() {
                page = 1;
                loadOtherVideos();
            }

            @Override
            public void loadMore() {
                page++;
                loadOtherVideos();
            }

            @Override
            public void errorClick() {
                page = 1;
                loadOtherVideos();
            }

            @Override
            public void emptyClick() {
                page = 1;
                loadOtherVideos();
            }
        });
    }

    private void loadOtherVideos() {

        Map<String, String> para = new HashMap<>();
        para.put("page", page + "");
        para.put("pageSize", "20");
        String tags = mVideo.getLabel();
        StringBuilder labelIds = new StringBuilder();
        if (tags != null) {
            tags = tags.replace(" ", "");
            String[] tmp = tags.split("#");
            if (tmp != null) {
                for (String str : tmp) {
                    labelIds.append(str);
                    labelIds.append(",");
                }
            }
            if (labelIds.length() > 0) {
                labelIds.deleteCharAt(labelIds.length() - 1);
            }
        }
        Log.d(TAG, "loadOtherVideos: labelIds " + labelIds.toString());
        para.put("labelId", labelIds.toString());
        String url = Api.RECOMMEND_VIDEOS;
        MyRequest.sendPostRequest(url, para, new MyResponseCallback<PreserveVideo>() {
            @Override
            public void onSuccessList(List<PreserveVideo> data) {
                dissLoad();
                if (page == 1) {
                    mOtherVideos.clear();
                }
                mOtherVideos.addAll(data);
                mAdapter.setVideos(mOtherVideos);
                if (page == 1) {
                    if (mOtherVideos.size() == 0) {
                        mRefreshLayout.showView(HotokRefreshLayout.ViewStatus.EMPTY_STATUS);
                    } else {
                        mRefreshLayout.showView(HotokRefreshLayout.ViewStatus.CONTENT_STATUS);
                    }
                }
                mRefreshLayout.finishLoadMore();
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                dissLoad();
                Toast.makeText(LandscapeVideoDetailActivity.this, getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();
                mRefreshLayout.finishLoadMore();
                mRefreshLayout.finishRefresh();
            }
        }, PreserveVideo.class, true);

    }

    private void initVideo() {
        if (mVideo == null) {
            return;
        }
        List<VideoModel> videoList = new ArrayList<>();
        videoList.add(new VideoModel(mVideo.getVideoUrl(), "", new StandardVideoController(LandscapeVideoDetailActivity.this), true));
        videoPlayer.setVideos(videoList);
        videoPlayer.setOnPlayListener(new ListIjkVideoView.OnPlayListener() {
            @Override
            public void onComplete() {
                mPlayComplete = true;
            }

            @Override
            public void onPrepared() {
                mPlayComplete = false;
            }

            @Override
            public void onError() {
                mPlayComplete = true;
            }

            @Override
            public void onInfo(int what, int extra) {
                mPlayComplete = false;
            }
        });
        videoPlayer.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_land_scape_video_detail;
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.pause();
        TCAgent.onPageEnd(this,TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mPlayComplete) {
            videoPlayer.start();
        }
        TCAgent.onPageStart(this,TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoPlayer.stopPlayback();
    }

    @OnClick({R.id.iv_back, R.id.iv_like, R.id.iv_download, R.id.iv_share, R.id.ll_like, R.id.ll_download, R.id.ll_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_like:

                boolean isFav = false;
                for (int i = 0; i < VideoManager.getInstace().getLandFavVideos().size(); i++) {
                    if (VideoManager.getInstace().getLandFavVideos().get(i).getId().equals(mVideo.getId())) {
                        isFav = true;
                        break;
                    }
                }
                if (isFav) {
                    Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.del_from_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().delVideoFromFav(mVideo, false);
                    ivLike.setImageResource(R.mipmap.ic_video_fav_gray);

                } else {
                    TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.ADD_FAV_LAND_VIDEO);
                    Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.add_to_fav_list), Toast.LENGTH_SHORT).show();
                    VideoManager.getInstace().addVideoToFav(mVideo, false);
                    ivLike.setImageResource(R.mipmap.icon_fav);
                }
                break;
            case R.id.ll_download:
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.DOWNLOAD_LAND_VIDEO);
                Toast.makeText(this, getString(R.string.add_to_download_list), Toast.LENGTH_SHORT).show();
                DownloadManager.getInstace().submitDownloadVideoTask(mVideo.getVideoUrl(), System.currentTimeMillis() + ".mp4", mVideo.getCover(), false);
                break;
            case R.id.ll_share:
//                loading(getString(R.string.loading));
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.SHARE_LAND_VIDEO);
                String url = mVideo.getVideoUrl();
                ShareUtils.shareFaceBook(this, "", "", url, new PlatformActionListener() {

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        dissLoad();
                        Toast.makeText(LandscapeVideoDetailActivity.this, "share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        dissLoad();
                        Toast.makeText(LandscapeVideoDetailActivity.this, "share fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        dissLoad();
                        Toast.makeText(LandscapeVideoDetailActivity.this, "share cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
