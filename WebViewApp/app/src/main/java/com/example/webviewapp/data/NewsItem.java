package com.example.webviewapp.data;

import java.util.ArrayList;
import java.util.List;

public class NewsItem {

    private String uniquekey;
    private String title;
    private String date;
    private String category;
    private String authorName;
    private String url;
    private List<String> thumbnailPics = new ArrayList<>();
    private String isContent;

    public NewsItem() {
    }

    public NewsItem(String uniquekey, String title, String date, String category, String authorName, String url, List<String> thumbnailPics, String isContent) {
        this.uniquekey = uniquekey;
        this.title = title;
        this.date = date;
        this.category = category;
        this.authorName = authorName;
        this.url = url;
        this.thumbnailPics = thumbnailPics;
        this.isContent = isContent;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getThumbnailPics() {
        return thumbnailPics;
    }

    public void setThumbnailPics(List<String> thumbnailPics) {
        this.thumbnailPics = thumbnailPics;
    }

    public String getIsContent() {
        return isContent;
    }

    public void setIsContent(String isContent) {
        this.isContent = isContent;
    }
}
