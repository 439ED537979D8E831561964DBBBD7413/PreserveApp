package com.social.preserve.model;

import java.io.Serializable;

/**
 * Created by pt198 on 24/08/2018.
 */

public class ThirdLoginUserData implements Serializable{
    private String sex;
    private String nickName;
    private String photo;
    private String openId;
    private String userId;
    //登录方式（1tiwwer 2facebook）
    private int loginType;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
