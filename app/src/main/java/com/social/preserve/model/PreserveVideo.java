package com.social.preserve.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pt198 on 09/01/2019.
 */

public class PreserveVideo implements Serializable{
    List<String> videoUrl;
    String described;
    String cover;
    String id;
    String label;
    String publisher;

    public void setPublisher(String content) {
        this.publisher = content;
    }

    public String getPublisher() {
        return publisher;
    }

    public List<String> getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(List<String> videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescribed() {
        return described;
    }

    public void setDescribed(String described) {
        this.described = described;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
