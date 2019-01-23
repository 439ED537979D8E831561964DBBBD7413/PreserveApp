package com.social.preserve.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.download.DownloadService;
import com.social.preserve.download.DownloadTask;
import com.social.preserve.download.VideoManager;
import com.social.preserve.model.ApkUpdateInfo;
import com.social.preserve.model.TabEntity;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.fragment.FavouriteFragment;
import com.social.preserve.ui.fragment.HomeFragment;
import com.social.preserve.ui.fragment.LandscapeVideoFragment;
import com.social.preserve.ui.fragment.LivePlayFragment;
import com.social.preserve.ui.fragment.MeFragment;
import com.social.preserve.ui.fragment.ShortVideoFrag;
import com.social.preserve.ui.views.UpDialog;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.Config;
import com.social.preserve.utils.PermissionUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED;
import static com.google.android.gms.common.ConnectionResult.SERVICE_MISSING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout_main)
    CommonTabLayout tabLayoutMain;
    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.ll_download_progress)
    View downloadProgressLayout;
    @BindView(R.id.tv_progress)
    TextView downloadProgressTv;
    @BindView(R.id.adView)
    AdView mAdView;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"", "", "", ""};
    private int[] mIconUnselectIds = {
            R.mipmap.tab_shortvideo_unselect, R.mipmap.tab_landvideo_unselect,
            R.mipmap.tab_fav_unselect, R.mipmap.tab_mine_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_shortvideo_select, R.mipmap.tab_landvideo_select,
            R.mipmap.tab_fav_select, R.mipmap.tab_mine_select};
    private HomeFragment mHomeFrag;
    private FavouriteFragment mFavFrag;
    private LivePlayFragment mLivePlayFrag;
    private MeFragment mMeFrag;
    private ShortVideoFrag mShortVideoFrag;
    private LandscapeVideoFragment mLandVideoFrag;
    private UpDialog updialog;
    private static final String TAG = "MainActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        PermissionUtils.setPermission(this, PermissionUtils.requestPermissions, PermissionUtils.REQUESTCODE_MULTI);
        fullScreen();
        initView();
        checkUpdate();
        initDownloadService();
        initVideoManager();
        initAdvertise();
    }

    private void initAdvertise() {
        int errorCode = GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(this);
        if( ConnectionResult.SUCCESS != errorCode )
        {
            String errorMsg="";
            if(errorCode==SERVICE_MISSING){
                errorMsg=getString(R.string.error_google_play_miss);
            }else if(errorCode==SERVICE_VERSION_UPDATE_REQUIRED){
                errorMsg=getString(R.string.error_google_play_update_requires);
            }else if(errorCode==SERVICE_DISABLED){
                errorMsg=getString(R.string.error_google_play_dissabled);
            }else{
                errorMsg=getString(R.string.error_google_play_loadfail);
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();

            return;
        }
//        AdvertisingIdClient.Info adInfo = null;
//        try {
//            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
//
//        } catch (IOException e) {
//            // Unrecoverable error connecting to Google Play services (e.g.,
//            // the old version of the service doesn't support getting AdvertisingId).
//            e.printStackTrace();
//            return;
//        } catch (GooglePlayServicesNotAvailableException e) {
//            // Google Play services is not available entirely.
//            e.printStackTrace();
//            return;
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return;
//        } catch (GooglePlayServicesRepairableException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return;
//        }
//        String id = adInfo.getId();
//        boolean isLAT = adInfo.isLimitAdTrackingEnabled();
//        Log.d(TAG, "getAdvertiseId: id "+id+",isLAT "+isLAT);


        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

    }

    private void initVideoManager(){
        VideoManager.getInstace().initData();
    }
    private void initDownloadService(){
        try {
            Intent intent=null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                intent=new Intent(this, DownloadService.class);
                startForegroundService(intent);
            } else {
                intent=new Intent(this, DownloadService.class);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initView(){
        showCommonTopBar(View.GONE);
        mHomeFrag=new HomeFragment();
        mFavFrag=new FavouriteFragment();
        mLivePlayFrag=new LivePlayFragment();
        mFavFrag=new FavouriteFragment();
        mMeFrag=new MeFragment();
        mShortVideoFrag=new ShortVideoFrag();
        mLandVideoFrag=new LandscapeVideoFragment();
        mFragments.add(mShortVideoFrag);
        mFragments.add(mLandVideoFrag);
        mFragments.add(mFavFrag);
        mFragments.add(mMeFrag);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayoutMain.setIconWidth(25);
        tabLayoutMain.setIconHeight(25);
        tabLayoutMain.setTabData(mTabEntities);
        tabLayoutMain.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpMain.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        vpMain.setOffscreenPageLimit(4);
        vpMain.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayoutMain.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpMain.setCurrentItem(0);
    }


    private void showUpWindow(final ApkUpdateInfo data) {

        //强制更新
        updialog = new UpDialog(MainActivity.this, R.style.loading_dialog, data);
        if (data.getForceUp() == 1) {
            updialog.setCancelable(false);
        } else {
            updialog.setCancelable(true);
        }
        updialog.show();

        updialog.setLinstener(new UpDialog.BtnClickLinstener() {
            @Override
            public void clickOk() {
                if (App.channel.equals(MainActivity.this.getResources().getString(R.string.google_channel))
                        || App.channel.equals(MainActivity.this.getResources().getString(R.string.google))
                        || App.channel.equals(MainActivity.this.getResources().getString(R.string.preserve))) {
                    launchAppDetail(getPackageName(), MainActivity.this.getResources().getString(R.string.google_market));
                } else {
                    //升级开始下载
                    setDownLoad(data.getUpdateUrl());
                }

            }

            @Override
            public void clickCancel() {
                if (data.getForceUp() != 1) {
                    updialog.dismiss();
                } else {

                }
            }
        });

    }


    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            Log.i("http-info", ">>>>>>>>>>>>>>>>>>>>>>>>upgrade");
            if (TextUtils.isEmpty(appPkg)) return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载包
     *
     * @param downloadurl 下载的url
     */

    protected void setDownLoad(String downloadurl) {
        // TODO Auto-generated method stub

        RequestParams params = new RequestParams(downloadurl);
        params.setAutoRename(true);//断点下载
        params.setSaveFilePath(Config.APKPATH);
        x.http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {


            }

            @Override
            public void onFinished() {


            }

            @Override
            public void onSuccess(File file) {
                Log.d(TAG, "onSuccess: ");
                if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                    Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.social.preserve", file);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    startActivity(intent);

                }

            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {
                Log.d(TAG, "onLoading: "+arg1);
                updialog.setMax((int) arg0);
                updialog.setProgress((int) arg1);
            }

            @Override
            public void onStarted() {

                updialog.setBtnDisable();
            }

            @Override
            public void onWaiting() {

            }
        });
    }
    /**
     * 升级信息
     *
     *
     */
    private void checkUpdate() {


        Map<String, String> para1 = new HashMap<>();
        para1.put("channelCode",App.channel);
        para1.put("platForm","1");
        para1.put("version",App.appVersionName);
        MyRequest.sendPostRequest(Api.APP_UPDATE, para1, new MyResponseCallback<ApkUpdateInfo>() {
            @Override
            public void onSuccess(ApkUpdateInfo data) {
                if(data.getForceUp()==1){
                    showUpWindow(data);
                }else {
                    if (data.getUpdate() == 1) {
                        showUpWindow(data);
                    } else {
//                        Toast.makeText(MainActivity.this, getString(R.string.no_new_version), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                Toast.makeText(MainActivity.this, getString(R.string.get_update_info_fail), Toast.LENGTH_SHORT).show();
            }
        }, ApkUpdateInfo.class, false);

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDownloadView();
        DownloadManager.getInstace().registerDownloadListener(mDownloadListener);
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager.getInstace().unregisterDownloadListener(mDownloadListener);
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    private void updateDownloadView(){
        Log.d(TAG, "updateUploadView: getUploadingTasks "+DownloadManager.getInstace().getDownloadingTasks().size());
        if(DownloadManager.getInstace().getDownloadingTasks().size()>0) {
            downloadProgressLayout.setVisibility(View.VISIBLE);
            DownloadTask task = DownloadManager.getInstace().getDownloadingTasks().get(0);
            if(task==null){
                return;
            }
            downloadProgressTv.setText(task.getFileName()+"  "+getString(R.string.label_downloading)+task.getProgress()+"%");

        }else{
            downloadProgressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    private DownloadManager.OnDownloadListener mDownloadListener =  new DownloadManager.OnDownloadListener() {
        @Override
        public void onStart(String taskId, String name) {

        }

        @Override
        public void onComplete(String taskId, String path,String name) {
            downloadProgressLayout.setVisibility(View.GONE);
        }

        @Override
        public void onFail(String taskId, String error,String name) {
            downloadProgressLayout.setVisibility(View.GONE);
        }

        @Override
        public void onProgress(String taskId, int progress,String name) {
            if(progress > 0 && progress == 100){
                downloadProgressLayout.setVisibility(View.GONE);
            }else if(progress > 0 && progress != 100){
                downloadProgressLayout.setVisibility(View.VISIBLE);
                downloadProgressTv.setText(name+"  "+getString(R.string.label_downloading)+progress+"%");
            }
        }
    } ;

    private class MyPagerAdapter extends FragmentPagerAdapter {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private Boolean isExit = false;

    private void exitBy2Click() {

        Timer tExit = null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(MainActivity.this, getString(R.string.press_the_exit_procedure_again), Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();

        }

    }
}
