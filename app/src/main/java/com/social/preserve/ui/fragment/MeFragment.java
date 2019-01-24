package com.social.preserve.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.account.AccountManager;
import com.social.preserve.model.ApkUpdateInfo;
import com.social.preserve.model.LanguageListModel;
import com.social.preserve.model.ThirdLoginResult;
import com.social.preserve.model.ThirdLoginUserData;
import com.social.preserve.network.MyException;
import com.social.preserve.network.MyRequest;
import com.social.preserve.network.MyResponseCallback;
import com.social.preserve.ui.activity.MyDownloadActivity;
import com.social.preserve.ui.activity.WebViewActivity;
import com.social.preserve.ui.adapter.LanguageListAdapter;
import com.social.preserve.ui.views.LanguageSeDialog;
import com.social.preserve.ui.views.UpDialog;
import com.social.preserve.utils.Api;
import com.social.preserve.utils.Config;
import com.social.preserve.utils.ImageTools2;
import com.social.preserve.utils.LanguageConfig;
import com.social.preserve.utils.ThirdLoginUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;

/**
 * Created by pt198 on 08/01/2019.
 */

public class MeFragment extends BaseFragment {
    @BindView(R.id.tv_facebook_login)
    TextView tvFacebookLogin;
    @BindView(R.id.ll_my_downloads)
    LinearLayout llMyDownloads;
    @BindView(R.id.ll_history)
    LinearLayout llHistory;
    @BindView(R.id.ll_language)
    LinearLayout llLanguage;
    @BindView(R.id.ll_check_update)
    LinearLayout llCheckUpdate;
    @BindView(R.id.ll_pic_policy)
    LinearLayout llPicPolicy;
    @BindView(R.id.ll_terms_use)
    LinearLayout llTermsUse;
    Unbinder unbinder;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.iv_avatar)
    ImageView avatarIv;
    @BindView(R.id.tv_name)
    TextView tvName;
    private List<LanguageListModel> mLanguages=new ArrayList<>();
    private static final String TAG = "MeFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_me, null);
        unbinder = ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {
        initLanguageInfo();
        initVersionCode();
        initUserInfo();
        Log.d(TAG, "initView: lan "+Locale.getDefault().toString());
    }

    private void initUserInfo(){
        if(AccountManager.getInstace().getNickName()==null){
            tvFacebookLogin.setText(R.string.face_login);
            tvName.setText("");
            tvName.setVisibility(View.GONE);
            avatarIv.setImageResource(R.mipmap.ic_default_avatar);
        }else {
            tvFacebookLogin.setText(R.string.login_out);
            tvName.setText(AccountManager.getInstace().getNickName());
            tvName.setVisibility(View.VISIBLE);
            ImageTools2.showAvatar(avatarIv, AccountManager.getInstace().getAvatar());
        }
    }

    private void initLanguageInfo(){
        Resources resources = getContext().getResources();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        tvLanguage.setText(config.locale.toString());
    }

    private void setLanguage(Locale language){
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale=language;
        resources.updateConfiguration(config, dm);
        tvLanguage.setText(config.locale.toString());
        App.getInstance().setLanguage(language);
        App.getInstance().getLanguageHelper().setValue("lan",language.toString());
    }

    private void initVersionCode(){
        PackageManager pm=getContext().getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(getContext().getPackageName(),0);
            String versionName=info.versionName;
            tvVersion.setText("V"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadLanguageData(){
        loading(getString(R.string.loading));
        mLanguages.clear();
        mLanguages.add(new LanguageListModel(LanguageConfig.ID_CHINESE,Locale.SIMPLIFIED_CHINESE.toString()));
        mLanguages.add(new LanguageListModel(LanguageConfig.ID_EN,Locale.ENGLISH.toString()));
        LanguageSeDialog language=new LanguageSeDialog(getContext(), mLanguages, new LanguageListAdapter.OnLanguageClickListener() {
            @Override
            public void onClick(int position) {
                if(position==0){
                    setLanguage(Locale.SIMPLIFIED_CHINESE);
                }else{
                    setLanguage(Locale.ENGLISH);
                }
            }
        });
        language.show();
        dissLoad();
    }

    private void chooseLanguage(){
        loadLanguageData();

    }

    private void thirdAuth(final int loginType) {
        ThirdLoginUtils.thirdAuth(loginType, new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
                if(isDetached()){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissLoad();
                    }
                });
                if (action == Platform.ACTION_USER_INFOR) {
                    PlatformDb platDB = platform.getDb();//获取数平台数据DB
                    //通过DB获取各种数据
                    String token = platDB.getToken();
                    String gender = platDB.getUserGender();
                    String userIcon = platDB.getUserIcon();
                    String userId = platDB.getUserId();
                    String name = platDB.getUserName();
                    String openId = userId;
                    final ThirdLoginUserData data = new ThirdLoginUserData();
                    data.setSex(gender);
                    data.setNickName(name);
                    data.setPhoto(userIcon);
                    data.setOpenId(openId);
                    data.setLoginType(loginType);
                    data.setUserId(userId);
                    Log.d(TAG, "onComplete: token " + token + ",gender " + gender + ",userIcon " + userIcon + ",openId " + openId + ",name " + name);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginIn(data);
                        }
                    });


                }
            }

            @Override
            public void onError(Platform platform, int i, final Throwable throwable) {
                Log.d(TAG, "onError: " + throwable.getCause()+","+throwable.getMessage());
                if(isDetached()){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissLoad();
                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d(TAG, "onCancel: ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissLoad();
                    }
                });
            }
        });
    }

    private Thread mThirdLoginTh;
    private void loginIn(final ThirdLoginUserData data){
        //invoke server interface

        loading(getString(R.string.loading));
        Map<String, String> para1 = new HashMap<>();
        para1.put("channelCode",App.channel);
        para1.put("nickName",data.getNickName());
        para1.put("openId",data.getOpenId());
        para1.put("type","1");
        para1.put("version",App.appVersionName);
        MyRequest.sendPostRequest(Api.THIRD_LOGIN, para1, new MyResponseCallback<ThirdLoginResult>() {
            @Override
            public void onSuccess(ThirdLoginResult loginResult) {
                dissLoad();
                tvFacebookLogin.setText(R.string.login_out);
                tvName.setText(data.getNickName());
                tvName.setVisibility(View.VISIBLE);
                ImageTools2.showAvatar(avatarIv,data.getPhoto());
                AccountManager.getInstace().setNickName(data.getNickName());
                AccountManager.getInstace().setAvatar(data.getPhoto());

            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                dissLoad();
                Toast.makeText(getContext(), getString(R.string.third_login_fail), Toast.LENGTH_SHORT).show();
            }
        }, ThirdLoginResult.class, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mThirdLoginTh!=null&&!mThirdLoginTh.isInterrupted()){
            mThirdLoginTh.interrupt();
        }
    }
//    private String mAccount;
//    private String mPwd;
    @OnClick({R.id.tv_facebook_login, R.id.ll_my_downloads, R.id.ll_history, R.id.ll_language, R.id.ll_check_update, R.id.ll_pic_policy, R.id.ll_terms_use})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_facebook_login:
                String text=((TextView)view).getText().toString();
                if(text.equals(getString(R.string.face_login))){
                    loading(getResources().getString(R.string.loading));
                    mThirdLoginTh = new Thread() {
                        @Override
                        public void run() {
                            thirdAuth(ThirdLoginUtils.LOGIN_TYPE_FACEBOOK);
                        }
                    };
                    mThirdLoginTh.start();
                }else{
                    tvFacebookLogin.setText(R.string.face_login);
                    tvName.setText("");
                    tvName.setVisibility(View.GONE);
                    avatarIv.setImageResource(R.mipmap.ic_default_avatar);
                    AccountManager.getInstace().setAccount(null);
                    AccountManager.getInstace().setAvatar(null);
                    AccountManager.getInstace().setNickName(null);
                    AccountManager.getInstace().setPwd(null);
                }

                break;
            case R.id.ll_my_downloads:
                Intent i=new Intent(getContext(), MyDownloadActivity.class);
                startActivity(i);
                break;
            case R.id.ll_history:
                break;
            case R.id.ll_language:
                chooseLanguage();
                break;
            case R.id.ll_check_update:
                checkUpdate();
                break;
            case R.id.ll_pic_policy:
                Intent policy=new Intent(getContext(), WebViewActivity.class);
                policy.putExtra("web_url",Api.WEB_URL_POLICY);
                policy.putExtra("title",getString(R.string.label_pic_policy));
                startActivity(policy);
                break;
            case R.id.ll_terms_use:
                Intent term=new Intent(getContext(), WebViewActivity.class);
                term.putExtra("web_url",Api.WEB_URL_TERMS_USE);
                term.putExtra("title",getString(R.string.label_terms_use));
                startActivity(term);
                break;
        }
    }
    private void checkUpdate() {

        loading(getString(R.string.loading));
        Map<String, String> para1 = new HashMap<>();
        para1.put("channelCode",App.channel);
        para1.put("platForm","1");
        para1.put("version",App.appVersionName);
        MyRequest.sendPostRequest(Api.APP_UPDATE, para1, new MyResponseCallback<ApkUpdateInfo>() {
            @Override
            public void onSuccess(ApkUpdateInfo data) {
                dissLoad();
                if(data.getForceUp()==1){
                    showUpWindow(data);
                }else {
                    if (data.getUpdate() == 1) {
                        showUpWindow(data);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_new_version), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(MyException e) {
                super.onFailure(e);
                dissLoad();
                Toast.makeText(getContext(), getString(R.string.get_update_info_fail), Toast.LENGTH_SHORT).show();
            }
        }, ApkUpdateInfo.class, false);

    }
    private void showUpWindow(final ApkUpdateInfo data) {

        //强制更新
        updialog = new UpDialog(getActivity(), R.style.loading_dialog, data);
        if (data.getForceUp() == 1) {
            updialog.setCancelable(false);
        } else {
            updialog.setCancelable(true);
        }
        updialog.show();

        updialog.setLinstener(new UpDialog.BtnClickLinstener() {
            @Override
            public void clickOk() {
                if (App.channel.equals(getResources().getString(R.string.google_channel))
                        || App.channel.equals(getResources().getString(R.string.google))
                        || App.channel.equals(getResources().getString(R.string.preserve))) {
                    launchAppDetail(getContext().getPackageName(), getResources().getString(R.string.google_market));
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

    UpDialog updialog;
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
                if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                    Uri apkUri = FileProvider.getUriForFile(getContext(), "com.social.preserve", file);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(new File(Config.DOWNLOAD_STORAGE_DIR, "update-release.apk")), "application/vnd.android.package-archive");
                    startActivity(intent);

                }

            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {
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
}
