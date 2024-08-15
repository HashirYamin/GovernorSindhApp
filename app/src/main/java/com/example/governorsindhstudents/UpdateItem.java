package com.example.governorsindhstudents;
public class UpdateItem {
    private String id;
    private String type;
    private String timestamp;

    public UpdateItem(String id, String type, String timestamp) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id; // Getter for id
    }

    public String getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
