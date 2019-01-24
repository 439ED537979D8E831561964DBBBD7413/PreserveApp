package com.social.preserve.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.social.preserve.R;
import com.social.preserve.model.PreserveVideo;
import com.social.preserve.model.TagModel;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.adapter.LandscapeVideoAdapter;
import com.social.preserve.ui.adapter.OtherVideoAdapter;
import com.social.preserve.ui.adapter.ShortVideoAdapter;
import com.social.preserve.ui.fragment.ShortVideoTypeFragment;
import com.social.preserve.ui.views.HotokRefreshLayout;
import com.social.preserve.ui.views.tagview.TagContainerLayout;
import com.social.preserve.ui.views.tagview.TagView;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.ScreenUtils;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pt198 on 10/01/2019.
 */

public class SearchVideoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    @BindView(R.id.search_RefreshLayout)
    HotokRefreshLayout refreshLayout;
    @BindView(R.id.tag_container)
    TagContainerLayout tagContainerLayout;
    private LandscapeVideoAdapter mLandAdapter;
    private ShortVideoAdapter mShortVideoAdapter;
    private List<PreserveVideo> mSearchDatas=new ArrayList<>();
    private int page;
    private boolean isShortVideo;
    private static final String TAG = "SearchVideoActivity";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        initTags();
    }
    private void getIntentData(){
        isShortVideo=getIntent().getBooleanExtra("isShortVideo",false);
    }
    private void initView(){
        showCommonTopBar(View.GONE);
        rvSearch.setHasFixedSize(true);
        if(isShortVideo) {
            GridLayoutManager manager = new GridLayoutManager(this,3);
            rvSearch.setLayoutManager(manager);
            rvSearch.addItemDecoration(new ShortVideoTypeFragment.SpacesItemDecoration(ScreenUtils.dip2px(this,2)));
            mShortVideoAdapter = new ShortVideoAdapter(this);
            rvSearch.setAdapter(mShortVideoAdapter);
        }else{
            LinearLayoutManager manager=new LinearLayoutManager(this);
            rvSearch.setLayoutManager(manager);
            mLandAdapter = new LandscapeVideoAdapter(this);
            rvSearch.setAdapter(mLandAdapter);
        }
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableAutoLoadMore(true);
        refreshLayout.setRefreshListener(new HotokRefreshLayout.OnRefreshLoadMoreListener() {
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
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_NULL:

                        break;
                    case EditorInfo.IME_ACTION_SEND:

                        break;
                    case EditorInfo.IME_ACTION_DONE:

                        break;
                    case EditorInfo.IME_ACTION_SEARCH:
                        searchData();
                        break;
                }

                return false;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==0){
                    tagContainerLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                }else{
                    tagContainerLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setFocusable(true);
                etSearch.setFocusableInTouchMode(true);
                etSearch.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) etSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etSearch, 0);
            }
        });

    }
    private List<String> mTags=new ArrayList<>();
    private List<TagModel> mTagModels=new ArrayList<>();
    private void initTags(){
        Map<String, String> para = new HashMap<>();
        para.put("page","1");
        para.put("pageSize","500");
        String url=Api.SEARCH_TAGS;
        MyRequest.sendPostRequest(url, para, new MyResponseCallback<TagModel>() {
            @Override
            public void onSuccessList(List<TagModel> data) {
                dissLoad();
                if(page==1) {
                    mTagModels.clear();
                    mTags.clear();
                }
                mTagModels.addAll(data);
                for(TagModel model:mTagModels){
                    mTags.add(model.getLabelName());
                }
                tagContainerLayout.setTags(mTags);
                if(mTags.size()==0) {
                    tagContainerLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }else{
                    tagContainerLayout.setVisibility(View.VISIBLE);
                    tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                        @Override
                        public void onTagClick(int position, String text) {
                            etSearch.setText(mTags.get(position));
                            etSearch.setSelection(mTags.get(position).length());
                            searchData();
                            tagContainerLayout.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onTagLongClick(int position, String text) {

                        }

                        @Override
                        public void onSelectedTagDrag(int position, String text) {

                        }

                        @Override
                        public void onTagCrossClick(int position) {

                        }
                    });
                    refreshLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                dissLoad();
                Toast.makeText(SearchVideoActivity.this, getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();

            }
        }, TagModel.class, true);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_video;
    }
    private void loadData(){
        Map<String, String> para = new HashMap<>();
        para.put("page",page+"");
        para.put("pageSize","20");
        para.put("classifyId","-1");
        para.put("keyword",etSearch.getText().toString());
        String url="";
        if(isShortVideo){
            url=Api.SHORT_VIDEO_LIST;
        }else{
            url=Api.LANDSCAPE_VIDEO_LIST;
        }
        MyRequest.sendPostRequest(url, para, new MyResponseCallback<PreserveVideo>() {
            @Override
            public void onSuccessList(List<PreserveVideo> data) {
                dissLoad();
                if(page==1) {
                    mSearchDatas.clear();
                }
                mSearchDatas.addAll(data);
                if(isShortVideo) {
                    mShortVideoAdapter.setVideos(mSearchDatas);
                }else{
                    mLandAdapter.setVideos(mSearchDatas);
                }
                if(page==1){
                    if(mSearchDatas.size()==0){
                        refreshLayout.showView(HotokRefreshLayout.ViewStatus.EMPTY_STATUS);
                    }else{
                        refreshLayout.showView(HotokRefreshLayout.ViewStatus.CONTENT_STATUS);
                    }
                }
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                dissLoad();
                Toast.makeText(SearchVideoActivity.this, getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
            }
        }, PreserveVideo.class, true);
    }
    private void searchData(){
        page=1;
        loading(getString(R.string.loading));
        loadData();

    }


    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this,TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this,TAG);
    }

    @OnClick({R.id.iv_back, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                searchData();
                break;
        }
    }
}
