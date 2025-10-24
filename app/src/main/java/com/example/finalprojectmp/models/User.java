package com.example.finalprojectmp.models;
import java.io.Serializable;

public abstract class User implements Serializable {
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected String profilePicture;
    protected String createdAt;
    protected String updatedAt;
    protected boolean isActive;

    // Abstract method to be implemented by subclasses
    public abstract String getUserType();

    // Constructors
    public User() {
        this.isActive = true;
    }

    public User(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isActive = true;
    }

    public User(int id, String name, String email, String password, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isActive = true;
    }

    // Common methods for all users
    public boolean verifyPassword(String inputPassword) {
        // In production, this should compare hashed passwords
        return this.password.equals(inputPassword);
    }

    public String getDisplayName() {
        return name != null ? name : email.split("@")[0];
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", type='" + getUserType() + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}