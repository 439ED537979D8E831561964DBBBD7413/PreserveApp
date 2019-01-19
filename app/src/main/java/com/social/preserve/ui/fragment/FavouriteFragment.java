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

import com.social.preserve.R;
import com.social.preserve.model.VideoType;
import com.social.preserve.ui.activity.SearchVideoActivity;
import com.social.preserve.utils.ScreenUtils;

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
import java.util.List;

/**
 * Created by pt198 on 08/01/2019.
 */

public class FavouriteFragment extends BaseFragment {
    private String[] mTitles;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MagicIndicator mMagicIndicator;
    private ViewPager segVp;
    private ImageView mSearchIv;
    private View mRootView;
    private String mFavShortVideoUrl="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.frag_favourite,null);
        initView(mRootView);
        return mRootView;
    }
    private void initView(View view){
        mTitles=new String[]{getString(R.string.label_short_video),getString(R.string.label_video)};
        mMagicIndicator = view.findViewById(R.id.magic_indicator_fav_frag);
        segVp = view.findViewById(R.id.vp_fav_frag);
        mFragments.add(FavShortVideoFragment.getNewInstance());
        mFragments.add(new FavVideoFragment());
        initIndicatorAndViewPager();
    }


    private void initIndicatorAndViewPager(){


        segVp.setOffscreenPageLimit(2);
        segVp.setAdapter(new FavouriteFragment.MyPagerAdapter(getFragmentManager()));
        segVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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
        mCommonNavigator.setAdjustMode(true);
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
//                simplePagerTitleView.setPadding(ScreenUtils.dip2px(getContext(),5), 0, ScreenUtils.dip2px(getContext(),5), 0);
                simplePagerTitleView.setNormalColor(getActivity().getResources().getColor(R.color.dark));
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
