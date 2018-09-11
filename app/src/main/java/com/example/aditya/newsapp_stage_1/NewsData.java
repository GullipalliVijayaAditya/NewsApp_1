package com.example.aditya.newsapp_stage_1;

public class NewsData {
    private String feedImage;
    private String author;
    private String title;
    private String dateOfPublish;
    private String webUrl;
    private String sectionName;

    public NewsData(String feedImage, String author, String title, String dateOfPublish, String webUrl, String sectionName) {
        this.feedImage = feedImage;
        this.author = author;
        this.title = title;
        this.dateOfPublish = dateOfPublish;
        this.webUrl = webUrl;
        this.sectionName = sectionName;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDateOfPublish() {
        return dateOfPublish;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getSectionName() {
        return sectionName;
    }
}

