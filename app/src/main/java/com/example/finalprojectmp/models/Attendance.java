package com.example.finalprojectmp.models;
import java.io.Serializable;

public class Attendance implements Serializable {
    private int id;
    private int userId;
    private int eventId;
    private int checkedIn;
    private String checkInTime;

    // Additional fields for display
    private String userName;
    private String eventTitle;
    private String eventDate;

    // Constructors
    public Attendance() {
    }

    public Attendance(int userId, int eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.checkedIn = 0;
    }

    public Attendance(int userId, int eventId, int checkedIn, String checkInTime) {
        this.userId = userId;
        this.eventId = eventId;
        this.checkedIn = checkedIn;
        this.checkInTime = checkInTime;
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

    public int getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(int checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
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

    // Helper methods
    public boolean isCheckedIn() {
        return checkedIn == 1;
    }

    public void markAsCheckedIn() {
        this.checkedIn = 1;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventId=" + eventId +
                ", checkedIn=" + checkedIn +
                ", checkInTime='" + checkInTime + '\'' +
                '}';
    }
}