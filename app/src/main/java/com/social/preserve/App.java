package com.social.preserve;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.header.DropBoxHeader;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.social.preserve.account.AccountManager;
import com.social.preserve.threadpool.FixedThreadPool;
import com.social.preserve.utils.Config;
import com.social.preserve.utils.PreferencesHelper;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TalkingDataEAuth;

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * Created by pt198 on 08/01/2019.
 */

public class App extends MultiDexApplication {
    public static int screenWidth ;
    public static int screenHeight ;
    public static int statuBarHeight ;
    private static App sInstance;
    public static Locale mLanguage;
    private String systemLang;
    //版本信息
    public static String appVersion = "",facebookName="", deviceId = "", deviceModel = "", osType = "", osVersion = "", appVersionName = "", channel = "", tcAgentDeviceId = "",googleAdvertiseId="",openId="";
    public static Activity currActivity;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static String locale;
    private PreferencesHelper mLanguageHelper;
    public static final int MSG_REPORT=0x766;
    public static final long MSG_REPORT_DURATION=50*1000;
    private static final String TAG = "App";

    public static App getInstance() {
        return sInstance;
    }

    public PreferencesHelper getLanguageHelper() {
        return mLanguageHelper;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                BallPulseFooter footer=new BallPulseFooter(context);
                footer.setAnimatingColor(Color.parseColor("#FF109F"));
                return footer;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private Handler mHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        initHandler();
        initConfig();
        initThirdLibrary();
        startReport();
    }
    
    
    private void startReport(){
        mHandler.sendEmptyMessage(MSG_REPORT);
    }
    
    private void initHandler(){
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_REPORT:
                        reportAnalyseData();
                        break;
                }
            }
        };
    }

    private void reportAnalyseData(){
        Log.d(TAG, "reportAnalyseData: ");
        boolean reportRes=false;
        if(!reportRes){
            mHandler.sendEmptyMessageDelayed(MSG_REPORT,MSG_REPORT_DURATION);
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public Locale getLanguage() {
        return mLanguage;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        registerActivityLifecycleCallbacks(callbacks);
    }

    Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Log.i("http-info","onActivityCreated");
            currActivity = activity;

        }

        @Override
        public void onActivityStarted(Activity activity) {
            //Log.i("http-info","onActivityStarted");

        }

        @Override
        public void onActivityResumed(Activity activity) {
            //Log.i("http-info","onActivityResumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            // Log.i("http-info","onActivityPaused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            // Log.i("http-info","onActivityStopped");

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            // Log.i("http-info","onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //Log.i("http-info","onActivityDestroyed");
            currActivity = null;

        }
    };
    public void setLanguage(Locale language) {
        this.mLanguage = language;
        this.locale=language.toString();
    }

    public void initGoogleAdId(){
        FixedThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    googleAdvertiseId = getGoogleAdId(App.getInstance());
                    Log.d(TAG, "initGoogleAdId: "+googleAdvertiseId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void initConfig(){
        Config.DOWNLOAD_STORAGE_DIR= Environment.getExternalStorageDirectory() + "/"+getResources().getString(R.string.app_name)+"/download/";
        Config.APKPATH= Config.DOWNLOAD_STORAGE_DIR+"update-release.apk";

        mLanguageHelper=new PreferencesHelper(this,"languageHelper");
        initGoogleAdId();
        widthAndHeight();
        deviceInfo();
        AccountManager.getInstace().init();
        registerActivityLifecycleCallback(callbacks);
        initStrictMode();
    }
    private void initStrictMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
    /**
     * 从apk中获取版本信息
     *
     * @param ctx
     * @param key 渠道前缀
     * @return
     */
    public static String getChannelFromApk(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }


    public static String getGoogleAdId(Context context) throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        pm.getPackageInfo("com.android.vending", 0);
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(
                "com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(
                        connection.getBinder());
                return adInterface.getId();
            } finally {
                context.unbindService(connection);
            }
        }
        return "";
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (this.retrieved)
                throw new IllegalStateException();
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean)
                throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }


    private void deviceInfo() {
        try {
            appVersion = getPackageInfo(this).versionCode + "";
            deviceModel = Build.MODEL;
            osType = Build.MANUFACTURER;
            osVersion = Build.VERSION.RELEASE; // android系统版本号
            appVersionName = getAppVersionName(this);
            channel = getChannelFromApk(this, "UMENG_CHANNEL");
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            //或者仅仅使用 locale = Locale.getDefault(); 不需要考虑接口 deprecated(弃用)问题
            systemLang = locale.getLanguage() + "-" + locale.getCountry();
            Log.e("获取手机信息", channel);
        } catch (Exception e) {
            Log.e("获取手机信息失败", e.getMessage());
        }
    }
    private void initThirdLibrary(){
        initTalkingData();
        initFireBase();
        initMobAds();
        initXutils();
        initImageLoader();

    }
    private void initFireBase(){
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }
    private void initMobAds(){
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
    }
    private void initTalkingData(){
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, Config.TALKING_DATA_APP_ID, Config.TALKING_DATA_CHANNEL_ID);
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true);
        tcAgentDeviceId=TCAgent.getDeviceId(this);
//        String eAppId="";
//        String eAuthSecretID = "";
//        TalkingDataEAuth.initEAuth(this.getApplicationContext(),eAppId,eAuthSecretID);

    }
    private void initXutils(){
        //引入xutil
        x.Ext.init(this);

        x.Ext.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
    private void initImageLoader(){
        initImageLoader(this);
    }
    private static void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "cache/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(5) //线程池内线程的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
//                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCache(new WeakMemoryCache())//使用弱引用策略防止OOM
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 8 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void widthAndHeight() {
        //窗口管理器
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        statuBarHeight = getStatusBarHeight(this);
    }
    /**
     * 获取手机状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
