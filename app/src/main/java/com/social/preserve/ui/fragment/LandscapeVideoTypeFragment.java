package com.social.preserve.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.social.preserve.R;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.adapter.LandscapeVideoAdapter;
import com.social.preserve.ui.adapter.ShortVideoAdapter;
import com.social.preserve.ui.views.HotokRefreshLayout;
import com.social.preserve.utils.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by pt198 on 10/01/2019.
 */

public class LandscapeVideoTypeFragment extends BaseFragment {
    @BindView(R.id.landscape_type_frag_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.landscape_type_frag_RefreshLayout)
    HotokRefreshLayout pullToRefreshLayout;
    Unbinder unbinder;
    private String mLoadTypeId;
    private int page = 1;
    private List<PreserveVideo> mVideos=new ArrayList<>();
    public static final String KEY_ID = "type_id";
    private LandscapeVideoAdapter mAdapter;
    private static final String TAG = "LandVideoTypeFragment";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoadTypeId = getArguments().getString(KEY_ID);
    }

    public static LandscapeVideoTypeFragment getNewInstance(String typeUrl) {
        LandscapeVideoTypeFragment frag = new LandscapeVideoTypeFragment();
        Bundle b = new Bundle();
        b.putString(KEY_ID, typeUrl);
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.frag_type_lanscape_video, null);
        unbinder = ButterKnife.bind(this, content);
        initView();
        loadData();
        return content;
    }


    private void loadData(){
        Map<String, String> para = new HashMap<>();
        para.put("page",page+"");
        para.put("pageSize","20");
        para.put("classifyId",mLoadTypeId);
        para.put("keyword","");
        MyRequest.sendPostRequest(Api.LANDSCAPE_VIDEO_LIST, para, new MyResponseCallback<PreserveVideo>() {
            @Override
            public void onSuccessList(List<PreserveVideo> data) {
                if(page==1){
                    mVideos.clear();
                }
                mVideos.addAll(data);
                mAdapter.setVideos(mVideos);
                if(page==1){
                    if(mVideos.size()==0){
                        pullToRefreshLayout.showView(HotokRefreshLayout.ViewStatus.EMPTY_STATUS);
                    }else{
                        pullToRefreshLayout.showView(HotokRefreshLayout.ViewStatus.CONTENT_STATUS);
                    }
                }
                pullToRefreshLayout.finishLoadMore();
                pullToRefreshLayout.finishRefresh();

            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                Toast.makeText(getContext(), getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();
                pullToRefreshLayout.finishLoadMore();
                pullToRefreshLayout.finishRefresh();
            }
        }, PreserveVideo.class, true);

//        mRecyclerView.post(new Runnable(){
//            @Override
//            public void run() {
//
//                //自动播放第一个
//                View view = mRecyclerView.getChildAt(0);
//                IjkVideoView ijkVideoView = view.findViewById(R.id.video_player);
//                ijkVideoView.start();
//            }
//
//        });
    }
//    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//
//        public SpacesItemDecoration(int space) {
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view,
//                                   RecyclerView parent, RecyclerView.State state) {
//
//
//            if (parent.getChildPosition(view) %2!=0) {
//                outRect.top = 0;
//                outRect.left = 0;
//                outRect.right = 0;
//                outRect.bottom = space;
//            }else{
//                outRect.top = 0;
//                outRect.left = 0;
//                outRect.right = space;
//                outRect.bottom = space;
//            }
//        }
//    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        pullToRefreshLayout.setEnableRefresh(true);
        pullToRefreshLayout.setEnableAutoLoadMore(true);
        pullToRefreshLayout.setRefreshListener(new HotokRefreshLayout.OnRefreshLoadMoreListener() {
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
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new LandscapeVideoAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setVideos(mVideos);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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
//
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            int firstVisibleItem, lastVisibleItem, visibleCount;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                switch (newState) {
//                    case SCROLL_STATE_IDLE: //滚动停止
//                        autoPlayVideo(recyclerView);
//                        break;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                visibleCount = lastVisibleItem - firstVisibleItem;//记录可视区域item个数
//            }
//
//            private void autoPlayVideo(RecyclerView view) {
//                //循环遍历可视区域videoview,如果完全可见就开始播放
//                for (int i = 0; i < visibleCount; i++) {
//                    if (view == null || view.getChildAt(i) == null) continue;
//                    IjkVideoView ijkVideoView = view.getChildAt(i).findViewById(R.id.video_player);
//                    if (ijkVideoView != null) {
//                        Rect rect = new Rect();
//                        ijkVideoView.getLocalVisibleRect(rect);
//                        int videoHeight = ijkVideoView.getHeight();
//                        if (rect.top == 0 && rect.bottom == videoHeight) {
//                            ijkVideoView.start();
//                            return;
//                        }
//                    }
//                }
//            }
//        });


    }
    private boolean mPlaying=false;
    @Override
    public void onPause() {
        super.onPause();
        if(VideoViewManager.instance().getCurrentVideoPlayer()!=null){
            mPlaying=VideoViewManager.instance().getCurrentVideoPlayer().isPlaying();
        }
        VideoViewManager.instance().pausePlayback();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPlaying) {
            VideoViewManager.instance().resumePlayback();
        }
    }
    //    @Override
//    public void onBackPressed() {
//        if (!VideoViewManager.instance().onBackPressed()){
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
