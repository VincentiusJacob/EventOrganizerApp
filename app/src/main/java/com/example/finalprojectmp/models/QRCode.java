package com.example.finalprojectmp.models;
import java.io.Serializable;

public class QRCode implements Serializable {
    private int id;
    private int userId;
    private int eventId;
    private String codeText;
    private String createdAt;

    // Additional fields for display
    private String userName;
    private String eventTitle;
    private String eventDate;
    private String eventTime;

    // Constructors
    public QRCode() {
    }

    public QRCode(int userId, int eventId, String codeText) {
        this.userId = userId;
        this.eventId = eventId;
        this.codeText = codeText;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventId=" + eventId +
                ", codeText='" + codeText + '\'' +
                '}';
    }
}
