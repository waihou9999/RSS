package com.alifabdulrahman.malaysiakinireader.model;

public class NewsSource {
    private String newsName;
    private String newsURL;

    public NewsSource(String newsName, String newsURL){
        this.newsName = newsName;
        this.newsURL = newsURL;
    }

    public void setNewsName(String newsName) {
        this.newsName = newsName;
    }

    public void setNewsURL(String newsURL) {
        this.newsURL = newsURL;
    }

    public String getNewsName() {
        return newsName;
    }

    public String getNewsURL() {
        return newsURL;
    }

    @Override
    public String toString(){
        return newsName;
    }
}
