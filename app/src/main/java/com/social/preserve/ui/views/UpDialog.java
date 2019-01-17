package com.social.preserve.ui.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.social.preserve.R;
import com.social.preserve.model.ApkUpdateInfo;

import static android.view.View.GONE;


public class UpDialog extends Dialog implements View.OnClickListener {


    /**
     * 上下文对象 *
     */
    Activity context;

    BtnClickLinstener linstener;



    TextView tvOk;
    TextView tvCancel;


    TextView tvMsg;
    TextView tvForceUp;
//    TextView tvTitle;
    LinearLayout llUp;
    LinearLayout llForceUp;
    ProgressBar progressBar;

    private ApkUpdateInfo info;
    private int max = 0;


    public UpDialog(Activity context) {
        super(context);
        this.context = context;

    }

    public UpDialog(Activity context, int theme, ApkUpdateInfo info) {
        super(context, theme);
        this.context = context;
        this.info = info;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.up_dialog);
        tvOk = (TextView) findViewById(R.id.tvOk);
        tvForceUp = (TextView) findViewById(R.id.tvForceUp);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
//        tvTitle= (TextView) findViewById(R.id.tvTitle);
        llForceUp= (LinearLayout) findViewById(R.id.llForceUp);
        llUp= (LinearLayout) findViewById(R.id.llUp);
        progressBar = findViewById(R.id.up_dlg_progressBar);
        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvForceUp.setOnClickListener(this);
        this.setCancelable(false);
    }

    @Override
    public void show() {
        super.show();
        if(null!=info){
            if(info.getForceUp()==1){
                //禁用返回键
                llUp.setVisibility(GONE);
                llForceUp.setVisibility(View.VISIBLE);
            }else if(info.getForceUp()!=1){
                //正常提醒
                llUp.setVisibility(View.VISIBLE);
                llForceUp.setVisibility(GONE);
            }
//            tvTitle.setText(context.getResources().getString(R.string.appUpdate));
            tvMsg.setText(info.getRemark().replace("\\n","\n"));
        }

    }

    
    @Override
    public void dismiss() {
        super.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOk:
                if(null!=linstener){

                    linstener.clickOk();
                }
                break;
            case R.id.tvCancel:
                if(null!=linstener){
                    linstener.clickCancel();
                }
                break;
            case R.id.tvForceUp:
                if(null!=linstener){

                    linstener.clickOk();
                }
                break;
            default:
                break;
        }
    }

    public BtnClickLinstener getLinstener() {
        return linstener;
    }

    public void setLinstener(BtnClickLinstener linstener) {
        this.linstener = linstener;
    }

    public void setMax(int max) {

        progressBar.setMax(max);
        this.max = max;
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);

    }


    public void setBtnDisable() {

        tvForceUp.setClickable(false);
        tvForceUp.setBackgroundColor(context.getResources().getColor(R.color.gray));


        tvForceUp.setClickable(false);
        tvOk.setBackgroundColor(context.getResources().getColor(R.color.gray));

        tvForceUp.setClickable(false);
        tvCancel.setBackgroundColor(context.getResources().getColor(R.color.gray));



    }



    public interface BtnClickLinstener{
        void clickOk();
        void clickCancel();
    }
}
