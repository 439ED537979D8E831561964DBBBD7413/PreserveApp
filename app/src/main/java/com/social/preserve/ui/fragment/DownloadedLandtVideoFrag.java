package com.social.preserve.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.ui.adapter.DownloadVideoAdapter;
import com.social.preserve.ui.adapter.ShortVideoAdapter;
import com.social.preserve.ui.views.HotokRefreshLayout;
import com.social.preserve.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLDisplay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pt198 on 15/01/2019.
 */

public class DownloadedLandtVideoFrag extends BaseFragment {
    @BindView(R.id.download_video_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.download_video_RefreshLayout)
    HotokRefreshLayout pullToRefreshLayout;
    Unbinder unbinder;

    private int page = 1;

    private DownloadVideoAdapter videoAdpater;
    private List<PreserveVideo> mVideos=new ArrayList<>();
    public static final String KEY_URL = "type_url";
    private static final String TAG = "DownloadedLandtVideoFrag";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content=inflater.inflate(R.layout.frag_donwloaded_land_video,null);
        unbinder = ButterKnife.bind(this, content);
        initView();
        loadData();
        return content;
    }
    public static DownloadedLandtVideoFrag getNewInstance() {
        DownloadedLandtVideoFrag frag = new DownloadedLandtVideoFrag();
        return frag;
    }

    private void initView() {

        pullToRefreshLayout.setEnableRefresh(true);
        pullToRefreshLayout.setEnableLoadMore(false);
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

        initRecyclerView();
    }
    private void loadData(){
//        for(int i=0;i<50;i++){
//            PreserveVideo video=new PreserveVideo();
//            video.setName("name"+i);
//            video.setUrl("http://vjs.zencdn.net/v/oceans.mp4");
//            mVideos.add(video);
//        }
        videoAdpater.setVideos(VideoManager.getInstace().getLandVideos());
        if(VideoManager.getInstace().getLandVideos().size()==0){
            pullToRefreshLayout.showView(HotokRefreshLayout.ViewStatus.EMPTY_STATUS);
        }else{
            pullToRefreshLayout.showView(HotokRefreshLayout.ViewStatus.CONTENT_STATUS);
        }
        pullToRefreshLayout.finishLoadMore();
        pullToRefreshLayout.finishRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        DownloadManager.getInstace().registerDownloadListener(mDownloadListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        DownloadManager.getInstace().unregisterDownloadListener(mDownloadListener);
    }

    private DownloadManager.OnDownloadListener mDownloadListener =  new DownloadManager.OnDownloadListener() {
        @Override
        public void onStart(String taskId, String name) {

        }

        @Override
        public void onComplete(String taskId, String path,String name) {
            videoAdpater.notifyDataSetChanged();
        }

        @Override
        public void onFail(String taskId, String error,String name) {
            videoAdpater.notifyDataSetChanged();
        }

        @Override
        public void onProgress(String taskId, int progress,String name) {
            videoAdpater.notifyDataSetChanged();
        }
    } ;
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {


            if (parent.getChildPosition(view) %2==1) {
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = space;
            }else if (parent.getChildPosition(view) %2==0){
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = space;
                outRect.bottom = space;
            }
        }
    }


    private void initRecyclerView() {

        videoAdpater = new DownloadVideoAdapter(getActivity(),false);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ScreenUtils.dip2px(getContext(),15)));
        mRecyclerView.setAdapter(videoAdpater);

    }
    public void delSelectedItems(){
        videoAdpater.delSelectedItems();
    }
    public void selAll(){
        videoAdpater.selAll();
    }
    public void cancelAll(){
        videoAdpater.cancelAll();
    }
    public void setEdit(boolean edit){
        videoAdpater.setEdit(edit);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
