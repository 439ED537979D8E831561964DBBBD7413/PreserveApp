package com.social.preserve.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.social.preserve.R;
import com.social.preserve.ui.fragment.DownloadedLandtVideoFrag;
import com.social.preserve.ui.fragment.DownloadedShortVideoFrag;
import com.social.preserve.ui.views.DownloadVideoMoreOpeWindow;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pt198 on 15/01/2019.
 */

public class MyDownloadActivity extends BaseActivity {
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_del)
    TextView tvDel;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    private String[] mTitles;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MagicIndicator mMagicIndicator;
    private ViewPager segVp;
    private ImageView mSearchIv;
    private View mRootView;
    private String mFavShortVideoUrl = "";
    DownloadedShortVideoFrag shortVideoFrag;
    DownloadedLandtVideoFrag landtVideoFrag;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = getWindow().getDecorView();
        ButterKnife.bind(this);
        initView(root);
    }

    private void initView(View view) {
        setTitle(getString(R.string.label_my_download));
        tvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvCommonRight.setText(getString(R.string.label_edit));
        showMoreOpeIv();
        tvCommonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvCommonRight.getText().toString().equals(getString(R.string.label_edit))){
//                    if(mCurrentTabIndex==0){
//                        shortVideoFrag.setEdit(true);
//                    } else{
//                        landtVideoFrag.setEdit(true);
//                    }
//                    tvCommonRight.setText(getString(R.string.cancel));
//                    llBottom.setVisibility(View.VISIBLE);
                }else{
                    if(mCurrentTabIndex==0){
                        shortVideoFrag.setEdit(false);
                    } else{
                        landtVideoFrag.setEdit(false);
                    }
                    tvCommonRight.setText(getString(R.string.label_edit));
                    showMoreOpeIv();
                    llBottom.setVisibility(View.GONE);
                }

            }
        });
        ivMoreOpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadVideoMoreOpeWindow.show(MyDownloadActivity.this, ivMoreOpe, new DownloadVideoMoreOpeWindow.OnEditListener() {
                    @Override
                    public void onEdit() {
                        if(mCurrentTabIndex==0){
                            shortVideoFrag.setEdit(true);
                        } else{
                            landtVideoFrag.setEdit(true);
                        }
                        tvCommonRight.setText(getString(R.string.cancel));
                        llBottom.setVisibility(View.VISIBLE);
                        hideMoreOpeIv();
                    }
                });
            }
        });
        mTitles = new String[]{getString(R.string.label_short_video), getString(R.string.label_video)};
        mMagicIndicator = view.findViewById(R.id.magic_indicator_fav_frag);
        segVp = view.findViewById(R.id.vp_fav_frag);
        shortVideoFrag=DownloadedShortVideoFrag.getNewInstance();
        landtVideoFrag=DownloadedLandtVideoFrag.getNewInstance();
        mFragments.add(shortVideoFrag);
        mFragments.add(landtVideoFrag);
        initIndicatorAndViewPager();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_download;
    }
    private int mCurrentTabIndex=0;
    private void initIndicatorAndViewPager() {


        segVp.setOffscreenPageLimit(2);
        segVp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        segVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTabIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initSegTab();
    }


    private CommonNavigator mCommonNavigator;

    private void initSegTab() {

        mCommonNavigator = new CommonNavigator(this);
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
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.gray));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.main));
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
                indicator.setLineWidth(UIUtil.dip2px(context,28));
                indicator.setLineHeight(UIUtil.dip2px(context,2));
                indicator.setRoundRadius(UIUtil.dip2px(context,3));
                indicator.setColors(getResources().getColor(R.color.main));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, segVp);
    }

    @OnClick({R.id.tv_all, R.id.tv_del})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_all:
                if(mCurrentTabIndex==0){
                    shortVideoFrag.selAll();
                }else{
                    landtVideoFrag.selAll();
                }
                break;
            case R.id.tv_del:
                if(mCurrentTabIndex==0){
                    shortVideoFrag.delSelectedItems();
                }else{
                    landtVideoFrag.delSelectedItems();
                }
                break;
        }
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
