package com.social.preserve.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.model.VideoType;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.activity.SearchVideoActivity;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.ScreenUtils;
import com.social.preserve.utils.TalkingDataKeyEvent;
import com.tendcloud.tenddata.TCAgent;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pt198 on 08/01/2019.
 */

public class ShortVideoFrag extends BaseFragment {
    private String[] mTitles;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MagicIndicator mMagicIndicator;
    private ViewPager segVp;
    private ImageView mSearchIv;
    private List<VideoType> mVideoTypes=new ArrayList<>();
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.frag_short_video,null);
        initView(mRootView);
        loadVideoTypes();
        return mRootView;

    }
    private void initView(View view){
        mMagicIndicator = view.findViewById(R.id.magic_indicator_short_video);
        segVp = view.findViewById(R.id.vp_short_video);
        mSearchIv=view.findViewById(R.id.iv_short_video_search);
        mSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCAgent.onEvent(App.getInstance(), TalkingDataKeyEvent.SEARCH_SHORT_VIDEO);
                Intent search=new Intent(getContext(), SearchVideoActivity.class);
                search.putExtra("isShortVideo",true);
                getContext().startActivity(search);
            }
        });
    }
    private void loadVideoTypes(){
        Map<String, String> para1 = new HashMap<>();
        para1.put("page","1");
        para1.put("pageSize","500");
        MyRequest.sendPostRequest(Api.SHORT_VIDEO_TYPE_LIST, para1, new MyResponseCallback<VideoType>() {
            @Override
            public void onSuccessList(List<VideoType> data) {
                mVideoTypes.clear();
                mVideoTypes.addAll(data);
                updateOnVideoTypesLoaded();
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                Toast.makeText(getContext(), getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();
            }
        }, VideoType.class, true);

    }
    private void updateOnVideoTypesLoaded(){
        mTitles=new String[mVideoTypes.size()];
        for(int i=0;i<mVideoTypes.size();i++){
            mTitles[i]=mVideoTypes.get(i).getClassifyName();
            mFragments.add(ShortVideoTypeFragment.getNewInstance(mVideoTypes.get(i).getClassifyId()));
        }
        initIndicatorAndViewPager();
    }
    private int mLastPos;
    private void initIndicatorAndViewPager(){


        segVp.setOffscreenPageLimit(mVideoTypes.size());
        segVp.setAdapter(new MyPagerAdapter(getFragmentManager()));
        segVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mLastPos!=position){
                    TCAgent.onPageStart(getContext(),mTitles[position]);
                    TCAgent.onPageEnd(getContext(),mTitles[mLastPos]);
                }
                mLastPos=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initSegTab();
    }


    private CommonNavigator mCommonNavigator;
    private void initSegTab() {

        mCommonNavigator = new CommonNavigator(getActivity());
        mCommonNavigator.setSkimOver(true);
        mCommonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {

                    SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                    simplePagerTitleView.setText(mTitles[index]);
                    simplePagerTitleView.setTextSize(16);
                    simplePagerTitleView.setPadding(ScreenUtils.dip2px(getContext(),10), 0, ScreenUtils.dip2px(getContext(),10), 0);
                    simplePagerTitleView.setNormalColor(getActivity().getResources().getColor(R.color.gray));
                    simplePagerTitleView.setSelectedColor(getActivity().getResources().getColor(R.color.main));
                    simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            segVp.setCurrentItem(index);
                        }
                    });
                    return simplePagerTitleView;


            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 4));
                indicator.setLineWidth(UIUtil.dip2px(getActivity(),28));
                indicator.setLineHeight(UIUtil.dip2px(getActivity(),2));
                indicator.setRoundRadius(UIUtil.dip2px(getActivity(),3));
                indicator.setColors(getActivity().getResources().getColor(R.color.main));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, segVp);
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


}
