package com.social.preserve.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.social.preserve.R;
import com.tendcloud.tenddata.TCAgent;


public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCommonTopBar(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirst();

            }
        },1500);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    public void isFirst() {

        Intent main=new Intent(this,MainActivity.class);
        startActivity(main);
        finish();
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
}
