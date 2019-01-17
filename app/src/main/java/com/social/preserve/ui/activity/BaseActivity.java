package com.social.preserve.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.social.preserve.R;
import com.social.preserve.ui.views.CenterLoadingView;


import butterknife.OnClick;

/**
 * Created by pt198 on 08/01/2019.
 */

public abstract class BaseActivity extends AppCompatActivity {
    ImageView tvCommonBack;
    TextView tvCommonTitle;
    TextView tvCommonRight;
    LinearLayout llCommonContent;
    FrameLayout flTopBar;
    private CenterLoadingView loading;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initView();
    }

    private void initView() {
        tvCommonBack=findViewById(R.id.tv_common_back);
        tvCommonTitle=findViewById(R.id.tv_common_title);
        tvCommonRight=findViewById(R.id.tv_common_right);
        llCommonContent=findViewById(R.id.ll_common_content);
        flTopBar=findViewById(R.id.fl_top_bar);
        if(getLayoutId()!=0) {
            View content = LayoutInflater.from(this).inflate(getLayoutId(), null);
            llCommonContent.addView(content);
        }
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
    protected void onDestroy() {
        super.onDestroy();
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
