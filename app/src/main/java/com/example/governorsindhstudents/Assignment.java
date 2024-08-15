package com.example.governorsindhstudents;

public class Assignment {
    private String title;
    private String description;
    private String url;

    public Assignment(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
