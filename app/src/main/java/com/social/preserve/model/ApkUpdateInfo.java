package com.social.preserve.model;

/**
 * Created by pt198 on 15/01/2019.
 */

public class ApkUpdateInfo {
    private int forceUp;
    private String remark;
    private String updateUrl;
    private int update;

    public int getForceUp() {
        return forceUp;
    }

    public void setForceUp(int forceUp) {
        this.forceUp = forceUp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}
