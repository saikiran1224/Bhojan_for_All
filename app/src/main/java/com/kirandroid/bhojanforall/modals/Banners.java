package com.kirandroid.bhojanforall.modals;

import java.io.Serializable;

public class Banners implements Serializable {

    private String name;
    private String url;

    public Banners(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Banners() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
