package com.example.knzwapp;

public class TravelNoteEntity {
    private String id;
    private String noteText;
    private String imagePath;
    private String location;
    private long timestamp;

    public TravelNoteEntity() {}

    public TravelNoteEntity(String noteText, String imagePath, String location, long timestamp) {
        this.noteText = noteText;
        this.imagePath = imagePath;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}