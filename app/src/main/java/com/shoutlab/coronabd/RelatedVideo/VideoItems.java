package com.shoutlab.coronabd.RelatedVideo;

public class VideoItems {

    private String thumbnail, title, description, url;

    public VideoItems(String thumbnail, String title, String description, String url) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
