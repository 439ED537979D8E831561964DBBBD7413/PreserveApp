package com.social.preserve.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.social.preserve.R;
import com.social.preserve.ui.views.CenterLoadingView;
import com.tendcloud.tenddata.TCAgent;


import butterknife.OnClick;

/**
 * Created by pt198 on 08/01/2019.
 */

public abstract class BaseActivity extends AppCompatActivity {
    ImageView tvCommonBack;
    TextView tvCommonTitle;
    TextView tvCommonRight;
    ImageView ivMoreOpe;
    LinearLayout llCommonContent;
    FrameLayout flTopBar;
    private CenterLoadingView loading;
    private ImmersionBar mImmersionBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootLayoutResId());
        initView();
    }
    protected int getRootLayoutResId(){
        return R.layout.activity_base;
    }
    public void showMoreOpeIv(){
        tvCommonRight.setVisibility(View.GONE);
        ivMoreOpe.setVisibility(View.VISIBLE);
    }
    public void hideMoreOpeIv(){
        tvCommonRight.setVisibility(View.VISIBLE);
        ivMoreOpe.setVisibility(View.GONE);
    }
    public void showRightText(String text){
        tvCommonRight.setText(text);
        tvCommonRight.setVisibility(View.VISIBLE);
        ivMoreOpe.setVisibility(View.GONE);
    }
    private void initView() {
        tvCommonBack=findViewById(R.id.tv_common_back);
        tvCommonTitle=findViewById(R.id.tv_common_title);
        tvCommonRight=findViewById(R.id.tv_common_right);
        llCommonContent=findViewById(R.id.ll_common_content);
        ivMoreOpe=findViewById(R.id.iv_more);
        flTopBar=findViewById(R.id.fl_top_bar);
        if(getLayoutId()!=0) {
            View content = LayoutInflater.from(this).inflate(getLayoutId(), null);
            llCommonContent.addView(content);
        }
        mImmersionBar = ImmersionBar.with(this);
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT){
            mImmersionBar.statusBarDarkFont(true,0.2f);  //状态栏字体是深色，不写默认为亮色
        }else{
            mImmersionBar.statusBarDarkFont(true);
        }
        mImmersionBar.statusBarColor(R.color.white)
                .navigationBarColor(R.color.black_53)
                .flymeOSStatusBarFontColor(R.color.black_53)  //修改flyme OS状态栏字体颜色
                .init();
    }

    public void fullScreen(){
        if(null==mImmersionBar){
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.statusBarColor(R.color.white)
                .transparentStatusBar()   //状态栏字体是深色，不写默认为亮色
                .fullScreen(true)
                .init();
    }


    public void showCommonTopBar(int visible){
        flTopBar.setVisibility(visible);
    }
    public void setTitle(int resId){
        tvCommonTitle.setText(resId);
    }
    public void setTitle(String title){
        tvCommonTitle.setText(title);
    }
    protected abstract int getLayoutId();

    @OnClick({R.id.tv_common_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_common_back:
                finish();
                break;

        }
    }

    public CenterLoadingView loading(String title) {
        if(null==loading){
            loading = new CenterLoadingView(BaseActivity.this);
            loading.setCanceledOnTouchOutside(false);
        }
        if (!this.isFinishing()){
            loading.setTitle(title);
            loading.show();
        }
        return loading;
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null){
            mImmersionBar.destroy();
        }
        try {
            if (loading != null && loading.isShowing()) {
                loading.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dissLoad()  {
        try {
            if (loading != null && loading.isShowing()) {
                if(!this.isFinishing()) {
                    loading.dismiss();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
