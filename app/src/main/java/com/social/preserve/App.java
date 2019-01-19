package com.social.preserve;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

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

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * Created by pt198 on 08/01/2019.
 */

public class App extends Application {
    public static int screenWidth ;
    public static int screenHeight ;
    public static int statuBarHeight ;
    private static App sInstance;
    private Locale mLanguage;
    private String systemLang;
    //版本信息
    public static String appVersion = "", deviceId = "", deviceModel = "", osType = "", osVersion = "", appVersionName = "", channel = "", appList = "";
    public static Activity currActivity;
    public static App getInstance() {
        return sInstance;
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
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        initConfig();
        initThirdLibrary();

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
    }

    private void initConfig(){
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
        initXutils();
        initImageLoader();
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
