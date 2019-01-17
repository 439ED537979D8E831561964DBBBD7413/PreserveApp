package com.social.preserve.download;

/**
 * Created by pt198 on 28/09/2018.
 */

public class DownloadTask {
    private String path;
    private String coverPath;
    private String id;
    private String fileName;
    private int progress;
    private String content;
    private boolean isShortVideo;
    public DownloadTask(String path, String id, String fileName, String content, String coverPath,boolean isShortVideo){
        this.path=path;
        this.id=id;
        this.fileName=fileName;
        this.content=content;
        this.coverPath = coverPath;
        this.isShortVideo=isShortVideo;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isShortVideo() {
        return isShortVideo;
    }

    public void setShortVideo(boolean shortVideo) {
        isShortVideo = shortVideo;
    }
}
