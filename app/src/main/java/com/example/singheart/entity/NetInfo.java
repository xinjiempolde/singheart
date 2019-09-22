package com.example.singheart.entity;

public class NetInfo {
    private String title;
    private String info;
    public NetInfo(String title,String subTitle){
        this.title = title;
        this.info = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
