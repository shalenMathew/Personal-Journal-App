package com.example.personaljournal.model;


import com.google.firebase.Timestamp;

public class Journal {

    String username;
    String title;
    String thoughts;
    String userId;
    String imageUrl;
    Timestamp timeAdded;

    String documentId;

    public Journal() {
    }

    public Journal(String username, String title, String thoughts, String userId, String imageUrl, Timestamp timeAdded, String documentId) {
        this.username = username;
        this.title = title;
        this.thoughts = thoughts;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.timeAdded = timeAdded;
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }


}
