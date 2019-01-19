package com.social.preserve.account;

import android.text.TextUtils;

import com.social.preserve.App;
import com.social.preserve.utils.PreferencesHelper;

/**
 * Created by pt198 on 16/01/2019.
 */

public class AccountManager {
    private PreferencesHelper mAccountHelper;
    private String mAccount;
    private String mPwd;
    private String mNickName;
    private String mAvatar;
    private static final String KEY_ACCOUNT = "user_name";
    private static final String KEY_PWD = "pwd";
    private static final String KEY_NICK_NAME = "nick_name";
    private static final String KEY_AVATAR = "avatar";
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
        String username = mAccountHelper.getValue(KEY_ACCOUNT);
        String pwd = mAccountHelper.getValue(KEY_PWD);

    }

    public String getAccount() {
        if (TextUtils.isEmpty(mAccount)) {
            return mAccountHelper.getValue(KEY_ACCOUNT);
        }
        return mAccount;
    }

    public String getPwd() {
        if (TextUtils.isEmpty(mPwd)) {
            return mAccountHelper.getValue(KEY_PWD);
        }
        return mPwd;
    }

    public void setAccount(String account) {
        this.mAccount = account;
        this.mAccountHelper.setValue(KEY_ACCOUNT, account);
    }
    public void setPwd(String pwd) {
        this.mPwd = pwd;
        this.mAccountHelper.setValue(KEY_PWD, pwd);
    }
    public void setAvatar(String avatar) {
        this.mAvatar = avatar;
        this.mAccountHelper.setValue(KEY_AVATAR, avatar);
    }
    public void setNickName(String nickName) {
        this.mNickName = nickName;
        this.mAccountHelper.setValue(KEY_NICK_NAME, nickName);
    }
    public String getAvatar() {
        if (TextUtils.isEmpty(mAvatar)) {
            return mAccountHelper.getValue(KEY_AVATAR);
        }
        return mAvatar;
    }
    public String getNickName() {
        if (TextUtils.isEmpty(mNickName)) {
            return mAccountHelper.getValue(KEY_NICK_NAME);
        }
        return mNickName;
    }
}
