package com.social.preserve.account;

import android.text.TextUtils;

import com.social.preserve.App;
import com.social.preserve.download.DownloadManager;
import com.social.preserve.utils.PreferencesHelper;

/**
 * Created by pt198 on 16/01/2019.
 */

public class AccountManager {
    private PreferencesHelper mAccountHelper;
    private String mUserName;
    private String mPwd;
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PWD = "pwd";

    private AccountManager() {

    }

    private static class SingleInstace {
        public static AccountManager instance = new AccountManager();
    }

    public static AccountManager getInstace() {
        return AccountManager.SingleInstace.instance;
    }

    public void init() {
        mAccountHelper = new PreferencesHelper(App.getInstance(), "account_helper");
        String username = mAccountHelper.getValue(KEY_USER_NAME);
        String pwd = mAccountHelper.getValue(KEY_PWD);

    }

    public String getUserName() {
        if (TextUtils.isEmpty(mUserName)) {
            return mAccountHelper.getValue(KEY_USER_NAME);
        }
        return mUserName;
    }

    public String getPwd() {
        if (TextUtils.isEmpty(mPwd)) {
            return mAccountHelper.getValue(KEY_PWD);
        }
        return mPwd;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
        this.mAccountHelper.setValue(KEY_USER_NAME, userName);
    }

    public void setPwd(String pwd) {
        this.mPwd = pwd;
        this.mAccountHelper.setValue(KEY_PWD, pwd);
    }
}
