package com.example.finalprojectmp.models;
import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    private int id;
    private String title;
    private String date;
    private String time;
    private String description;
    private int createdBy;
    private String createdAt;
    private String updatedAt;

    private ArrayList<Food> menus;
    private String organizerName;
    private int totalRsvp;
    private int totalCheckedIn;
    private boolean userHasRsvp;

    // Constructors
    public Event() {
        this.menus = new ArrayList<>();
    }

    public Event(String title, String date, String time, String description, int createdBy, ArrayList<Food> menus) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.createdBy = createdBy;
        this.menus = menus != null ? menus : new ArrayList<>();
    }

    public Event(int id, String title, String date, String time, String description, int createdBy, ArrayList<Food> menus) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.createdBy = createdBy;
        this.menus = menus != null ? menus : new ArrayList<>();
    }

    // Food management methods
    public void addFood(Food food) {
        if (menus == null) {
            menus = new ArrayList<>();
        }
        menus.add(food);
    }

    public void removeFood(Food food) {
        if (menus != null) {
            menus.remove(food);
        }
    }

    public ArrayList<Food> getAvailableFood() {
        ArrayList<Food> availableFood = new ArrayList<>();
        if (menus != null) {
            for (Food food : menus) {
                if (food.isAvailable()) {
                    availableFood.add(food);
                }
            }
        }
        return availableFood;
    }

    public ArrayList<Food> getMenus() {
        return menus;
    }

    public void setMenus(ArrayList<Food> menus) {
        this.menus = menus != null ? menus : new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public int getTotalRsvp() {
        return totalRsvp;
    }

    public void setTotalRsvp(int totalRsvp) {
        this.totalRsvp = totalRsvp;
    }

    public int getTotalCheckedIn() {
        return totalCheckedIn;
    }

    public void setTotalCheckedIn(int totalCheckedIn) {
        this.totalCheckedIn = totalCheckedIn;
    }

    public boolean isUserHasRsvp() {
        return userHasRsvp;
    }

    public void setUserHasRsvp(boolean userHasRsvp) {
        this.userHasRsvp = userHasRsvp;
    }

    public String getFullDateTime() {
        return date + " " + time;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", menus=" + (menus != null ? menus.size() : 0) + " items" +
                '}';
    }
}