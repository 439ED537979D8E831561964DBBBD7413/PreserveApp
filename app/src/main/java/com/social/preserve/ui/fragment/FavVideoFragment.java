package com.social.preserve.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.social.preserve.R;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.adapter.LandscapeVideoAdapter;
import com.social.preserve.ui.adapter.OtherVideoAdapter;
import com.social.preserve.ui.views.HotokRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by pt198 on 11/01/2019.
 */

public class FavVideoFragment extends BaseFragment {
    @BindView(R.id.fav_video_rv)
    RecyclerView favVideoRv;
    @BindView(R.id.fav_video_RefreshLayout)
    HotokRefreshLayout favVideoRefreshLayout;
    Unbinder unbinder;
    private OtherVideoAdapter mAdapter;
    private int page=1;
    private List<PreserveVideo> mVideos=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.frag_fav_video, null);
        unbinder = ButterKnife.bind(this, content);
        initView();
        loadData();
        return content;
    }

    private void loadData(){
//        for(int i=0;i<20;i++){
//            PreserveVideo video=new PreserveVideo();
//            video.setName("fav"+i);
//            video.setUrl("http://vjs.zencdn.net/v/oceans.mp4");
//            mVideos.add(video);
//        }

        mAdapter.setVideos(VideoManager.getInstace().getLandFavVideos());
        if(VideoManager.getInstace().getLandFavVideos().size()==0){
            favVideoRefreshLayout.showView(HotokRefreshLayout.ViewStatus.EMPTY_STATUS);
        }else{
            favVideoRefreshLayout.showView(HotokRefreshLayout.ViewStatus.CONTENT_STATUS);
        }
        favVideoRefreshLayout.finishLoadMore();
        favVideoRefreshLayout.finishRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        favVideoRefreshLayout.setEnableRefresh(true);
        favVideoRefreshLayout.setEnableLoadMore(false);
        favVideoRefreshLayout.setRefreshListener(new HotokRefreshLayout.OnRefreshLoadMoreListener() {
            @Override
            public void refresh() {
                page = 1;
                loadData();
            }

            @Override
            public void loadMore() {

                page++;
                loadData();

            }

            @Override
            public void errorClick() {
                page = 1;
                loadData();
            }

            @Override
            public void emptyClick() {
                page = 1;
                loadData();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        favVideoRv.setLayoutManager(layoutManager);
        mAdapter=new OtherVideoAdapter(getContext());
        favVideoRv.setAdapter(mAdapter);
        favVideoRv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                IjkVideoView ijkVideoView = view.findViewById(R.id.video_player);
                if (ijkVideoView != null && !ijkVideoView.isFullScreen()) {
                    ijkVideoView.stopPlayback();
                }
            }
        });

        favVideoRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem, visibleCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        autoPlayVideo(recyclerView);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;//记录可视区域item个数
            }

            private void autoPlayVideo(RecyclerView view) {
                //循环遍历可视区域videoview,如果完全可见就开始播放
                for (int i = 0; i < visibleCount; i++) {
                    if (view == null || view.getChildAt(i) == null) continue;
                    IjkVideoView ijkVideoView = view.getChildAt(i).findViewById(R.id.video_player);
                    if (ijkVideoView != null) {
                        Rect rect = new Rect();
                        ijkVideoView.getLocalVisibleRect(rect);
                        int videoHeight = ijkVideoView.getHeight();
                        if (rect.top == 0 && rect.bottom == videoHeight) {
                            ijkVideoView.start();
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
