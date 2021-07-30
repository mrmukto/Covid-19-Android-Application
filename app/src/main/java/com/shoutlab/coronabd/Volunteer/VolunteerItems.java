package com.shoutlab.coronabd.Volunteer;

public class VolunteerItems {
    private String title, logo, url;

    public VolunteerItems(String title, String logo, String url) {
        this.title = title;
        this.logo = logo;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
