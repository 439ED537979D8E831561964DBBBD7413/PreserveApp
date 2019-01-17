package com.social.preserve.ui.views;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.social.preserve.R;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by pt198 on 29/12/2018.
 */

public class HotokRefreshLayout extends SmartRefreshLayout {
    private View mEmptyView;
    private View mErrorView;
    private View mContent;
    private View mHeader;
    private View mFooter;
    private View mRootView;
    private OnRefreshLoadMoreListener mRefreshListener;
    public static class ViewStatus{
        public static final int EMPTY_STATUS=1;
        public static final int CONTENT_STATUS=2;
        public static final int ERROR_STATUS=3;
        @IntDef({CONTENT_STATUS,EMPTY_STATUS,ERROR_STATUS})
        @Retention(RetentionPolicy.SOURCE)
        public @interface VIEW_STATE {

        }
    }


    public HotokRefreshLayout(Context context) {
        super(context);

        initEmptyErrorLayout(context);
        init();
    }

    public HotokRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initEmptyErrorLayout(context);
        init();
    }

    public HotokRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initEmptyErrorLayout(context);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContent=mRefreshContent==null?null:mRefreshContent.getView();
        mFooter=mRefreshFooter==null?null:mRefreshFooter.getView();
        mHeader=mRefreshHeader==null?null:mRefreshHeader.getView();
    }

    public void setEmptyView(View empty){
        this.mEmptyView=empty;
    }

    private void init(){
        setOnRefreshLoadMoreListener(new com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(mRefreshListener!=null){
                    mRefreshListener.loadMore();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(mRefreshListener!=null){
                    mRefreshListener.refresh();
                }
            }
        });
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRefreshListener!=null){
                    mRefreshListener.emptyClick();
                }
            }
        });
        mErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRefreshListener!=null){
                    mRefreshListener.errorClick();
                }
            }
        });
    }
    public interface OnRefreshLoadMoreListener{
        void refresh();
        void loadMore();
        void errorClick();
        void emptyClick();
    }
    public void setRefreshListener(OnRefreshLoadMoreListener listener){
        mRefreshListener=listener;
    }
    private void initEmptyErrorLayout(Context context){
        mEmptyView=LayoutInflater.from(context).inflate(R.layout.layout_empty,null);
        mErrorView=LayoutInflater.from(context).inflate(R.layout.layout_error,null);
//        addView(mEmptyView, MATCH_PARENT, MATCH_PARENT);
//        addView(mErrorView, MATCH_PARENT, MATCH_PARENT);
    }
    public void showView(@ViewStatus.VIEW_STATE int state){
        switchView(state);
    }
    private void hideView(View view) {
        if (view != null)
            view.setVisibility(GONE);
    }
    private void switchView(@ViewStatus.VIEW_STATE int status) {
        switch (status) {
            case ViewStatus.CONTENT_STATUS:
//                hideView(mEmptyView);
//                hideView(mErrorView);
//                if(mContent!=null) {
//                    mContent.setVisibility(VISIBLE);
//                }
//                if(mHeader!=null) {
//                    mHeader.setVisibility(VISIBLE);
//                }
//                if(mFooter!=null) {
//                    mFooter.setVisibility(VISIBLE);
//                }
                setRefreshContent(mContent);
                break;
            case ViewStatus.EMPTY_STATUS:
//                hideView(mHeader);
//                hideView(mFooter);
//                hideView(mContent);
//                hideView(mErrorView);
//                if(mEmptyView!=null) {
//                    mEmptyView.setVisibility(VISIBLE);
//                }
                setRefreshContent(mEmptyView);
                break;
            case ViewStatus.ERROR_STATUS:
//                hideView(mHeader);
//                hideView(mFooter);
//                hideView(mContent);
//                hideView(mEmptyView);
//                if(mErrorView!=null) {
//                    mErrorView.setVisibility(VISIBLE);
//                }
                setRefreshContent(mErrorView);
                break;
            default:

//                hideView(mEmptyView);
//                hideView(mErrorView);
//                if(mContent!=null) {
//                    mContent.setVisibility(VISIBLE);
//                }
//                if(mHeader!=null) {
//                    mHeader.setVisibility(VISIBLE);
//                }
//                if(mFooter!=null) {
//                    mFooter.setVisibility(VISIBLE);
//                }
                setRefreshContent(mContent);
                break;
        }
    }
}
