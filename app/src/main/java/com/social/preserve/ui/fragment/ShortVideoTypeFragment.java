package com.social.preserve.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.social.preserve.R;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.model.VideoType;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.adapter.ShortVideoAdapter;
import com.social.preserve.ui.views.HotokRefreshLayout;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pt198 on 09/01/2019.
 */

public class ShortVideoTypeFragment extends BaseFragment {
    @BindView(R.id.type_frag_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.type_frag_RefreshLayout)
    HotokRefreshLayout pullToRefreshLayout;
    Unbinder unbinder;
    private String mLoadId;


    private boolean isInit;

    private int page = 1;

    private ShortVideoAdapter videoAdpater;
    private List<PreserveVideo> mVideos=new ArrayList<>();
    public static final String KEY_ID = "type_id";
    private static final String TAG = "ShortVideoTypeFragment";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoadId = getArguments().getString(KEY_ID);
    }

    public static ShortVideoTypeFragment getNewInstance(String typeId) {
        ShortVideoTypeFragment frag = new ShortVideoTypeFragment();
        Bundle b = new Bundle();
        b.putString(KEY_ID, typeId);
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.frag_type, null);
        unbinder = ButterKnife.bind(this, content);
        initView();
        loadData();
        return content;
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

        initRecyclerView();
    }


    private void loadData(){
        final Map<String, String> para = new HashMap<>();
        para.put("page",page+"");
        para.put("pageSize","20");
        para.put("classifyId",mLoadId);
        para.put("keyword","");
        MyRequest.sendPostRequest(Api.SHORT_VIDEO_LIST, para, new MyResponseCallback<PreserveVideo>() {
            @Override
            public void onSuccessList(List<PreserveVideo> data) {
                if(page==1){
                    mVideos.clear();
                }
                mVideos.addAll(data);
                videoAdpater.setVideos(mVideos);
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


    }
    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {


            if (parent.getChildPosition(view) %3==2) {
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = space;
            }else if (parent.getChildPosition(view) %3==1){
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = space;
                outRect.bottom = space;
            }else{
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = space;
                outRect.bottom = space;
            }
        }
    }


    private void initRecyclerView() {

        videoAdpater = new ShortVideoAdapter(getActivity());

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ScreenUtils.dip2px(getContext(),2)));
        mRecyclerView.setAdapter(videoAdpater);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
