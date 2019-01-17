package com.social.preserve.model;

import java.io.Serializable;

/**
 * Created by pt198 on 09/01/2019.
 */

public class PreserveVideo implements Serializable{
    String videoUrl;
    String described;
    String cover;
    String id;
    String label;
    String title;

    public void setTitle(String content) {
        this.title = content;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescribed() {
        return described;
    }

    public void setDescribed(String name) {
        this.described = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return videoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String tags) {
        this.label = tags;
    }
}
