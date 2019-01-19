package com.social.preserve.ui.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.social.preserve.R;
import com.social.preserve.ui.views.X5WebView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class WebViewActivity extends BaseActivity {
    X5WebView webView;
    TextView tvTitle,tvClose;
    ImageView ivBack;
    private com.tencent.smtt.sdk.WebViewClient client = new com.tencent.smtt.sdk.WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
            //if("http://hotokyjy.8b652a741ff14ec381374edb1604b123.xyz/".equals(url)){

            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            dissLoad();
            super.onPageFinished(webView, s);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCommonTopBar(View.GONE);
        tvTitle = findViewById(R.id.webview_tv_title);
        tvTitle.setText(getIntent().getStringExtra("title"));
        tvTitle.setMaxEms(10);
        tvTitle.setSingleLine();
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        ivBack = findViewById(R.id.webview_iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvClose = findViewById(R.id.webview_tv_close);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        commonWhiteTitle.setTitleText(getIntent().getStringExtra(Constant.EXTRA_1));
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        webView = findViewById(R.id.official_webview);
        initWebViewSettings();
        webView.loadUrl(getIntent().getStringExtra("web_url"));
        loading(getString(R.string.loading));
    }

    private void initWebViewSettings(){
        //使用setting
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);     // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true);       // 允许访问文件
        webSettings.setBuiltInZoomControls(true);   // 设置显示缩放按钮
        webSettings.setSupportZoom(true);           // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(client);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        //处理webview的内存泄漏
        if( webView!=null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);

            webView.clearView();
            webView.removeAllViews();
            webView.destroy();

        }
        super.onDestroy();
    }
}
