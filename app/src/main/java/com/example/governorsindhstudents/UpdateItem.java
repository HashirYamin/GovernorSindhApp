package com.example.governorsindhstudents;
public class UpdateItem {
    private String id;
    private String type;
    private String timestamp;
    private boolean isNew;

    public UpdateItem(String id, String type, String timestamp, boolean isNew) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.isNew = isNew;
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
    public boolean isNew() {
        return isNew;
    }
}
