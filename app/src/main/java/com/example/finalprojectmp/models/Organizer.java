package com.example.finalprojectmp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Organizer class - Extends User with organizer-specific attributes
 */
public class Organizer extends User {
    // Organizer-specific attributes
    private String organizationName;
    private String organizationType;  // "company", "university", "ngo", "individual"
    private String organizationAddress;
    private String organizationWebsite;
    private String designation;  // Job title/position
    private String department;
    private int totalEventsCreated;
    private int totalEventsCompleted;
    private int totalAttendeesManaged;
    private float averageEventRating;
    private boolean isVerified;  // Verified organizer status
    private String verificationDate;
    private String bankAccountDetails;  // For payment/refunds
    private String taxId;  // For official events
    private double eventBudgetLimit;
    private String specializations;  // "conferences,workshops,seminars,social"

    // Lists to store organizer's activities (not stored in DB, populated at runtime)
    private transient List<Event> myEvents;
    private transient List<EventStats> eventStatistics;

    // Constructors
    public Organizer() {
        super();
        this.totalEventsCreated = 0;
        this.totalEventsCompleted = 0;
        this.totalAttendeesManaged = 0;
        this.averageEventRating = 0.0f;
        this.isVerified = false;
        this.eventBudgetLimit = 0.0;
        initializeLists();
    }

    public Organizer(String name, String email, String password, String phoneNumber) {
        super(name, email, password, phoneNumber);
        this.totalEventsCreated = 0;
        this.totalEventsCompleted = 0;
        this.totalAttendeesManaged = 0;
        this.averageEventRating = 0.0f;
        this.isVerified = false;
        this.eventBudgetLimit = 0.0;
        initializeLists();
    }

    public Organizer(int id, String name, String email, String password, String phoneNumber,
                     String organizationName, String organizationType) {
        super(id, name, email, password, phoneNumber);
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.totalEventsCreated = 0;
        this.totalEventsCompleted = 0;
        this.totalAttendeesManaged = 0;
        this.averageEventRating = 0.0f;
        this.isVerified = false;
        this.eventBudgetLimit = 0.0;
        initializeLists();
    }

    private void initializeLists() {
        this.myEvents = new ArrayList<>();
        this.eventStatistics = new ArrayList<>();
    }

    // Implementation of abstract method
    @Override
    public String getUserType() {
        return "organizer";
    }

    // Organizer-specific methods
    public boolean canCreateEvent() {
        // Check if organizer is verified and active
        return isVerified && isActive;
    }

    public boolean hasReachedEventLimit(int monthlyLimit) {
        // Check if organizer has reached monthly event creation limit
        // This would need to check events created in current month
        return totalEventsCreated >= monthlyLimit;
    }

    public void incrementEventsCreated() {
        this.totalEventsCreated++;
    }

    public void incrementEventsCompleted() {
        this.totalEventsCompleted++;
    }

    public void addAttendees(int count) {
        this.totalAttendeesManaged += count;
    }

    public float getCompletionRate() {
        if (totalEventsCreated > 0) {
            return (float) totalEventsCompleted / totalEventsCreated * 100;
        }
        return 0.0f;
    }

    public boolean isExperiencedOrganizer() {
        return totalEventsCompleted >= 10 && averageEventRating >= 4.0f;
    }

    public boolean canManageLargeEvents() {
        return isVerified && totalEventsCompleted >= 5;
    }

    public String getOrganizerBadge() {
        if (totalEventsCompleted >= 50 && averageEventRating >= 4.5f) {
            return "PLATINUM";
        } else if (totalEventsCompleted >= 25 && averageEventRating >= 4.0f) {
            return "GOLD";
        } else if (totalEventsCompleted >= 10 && averageEventRating >= 3.5f) {
            return "SILVER";
        } else if (totalEventsCompleted >= 5) {
            return "BRONZE";
        } else {
            return "BEGINNER";
        }
    }

    public boolean specializesIn(String eventType) {
        if (specializations == null || specializations.isEmpty()) {
            return true;  // If no specializations set, can organize all types
        }
        return specializations.toLowerCase().contains(eventType.toLowerCase());
    }

    // Getters and Setters
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    public String getOrganizationWebsite() {
        return organizationWebsite;
    }

    public void setOrganizationWebsite(String organizationWebsite) {
        this.organizationWebsite = organizationWebsite;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getTotalEventsCreated() {
        return totalEventsCreated;
    }

    public void setTotalEventsCreated(int totalEventsCreated) {
        this.totalEventsCreated = totalEventsCreated;
    }

    public int getTotalEventsCompleted() {
        return totalEventsCompleted;
    }

    public void setTotalEventsCompleted(int totalEventsCompleted) {
        this.totalEventsCompleted = totalEventsCompleted;
    }

    public int getTotalAttendeesManaged() {
        return totalAttendeesManaged;
    }

    public void setTotalAttendeesManaged(int totalAttendeesManaged) {
        this.totalAttendeesManaged = totalAttendeesManaged;
    }

    public float getAverageEventRating() {
        return averageEventRating;
    }

    public void setAverageEventRating(float averageEventRating) {
        this.averageEventRating = averageEventRating;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getBankAccountDetails() {
        return bankAccountDetails;
    }

    public void setBankAccountDetails(String bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public double getEventBudgetLimit() {
        return eventBudgetLimit;
    }

    public void setEventBudgetLimit(double eventBudgetLimit) {
        this.eventBudgetLimit = eventBudgetLimit;
    }

    public String getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }

    public List<Event> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(List<Event> myEvents) {
        this.myEvents = myEvents;
    }

    public List<EventStats> getEventStatistics() {
        return eventStatistics;
    }

    public void setEventStatistics(List<EventStats> eventStatistics) {
        this.eventStatistics = eventStatistics;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organizationName + '\'' +
                ", type='" + organizationType + '\'' +
                ", eventsCreated=" + totalEventsCreated +
                ", verified=" + isVerified +
                ", badge='" + getOrganizerBadge() + '\'' +
                '}';
    }

    // Inner class for event statistics
    public static class EventStats {
        public int eventId;
        public String eventTitle;
        public int totalRsvps;
        public int totalAttended;
        public float attendanceRate;
        public double revenue;
        public float rating;

        public EventStats() {}

        public EventStats(int eventId, String eventTitle) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
        }
    }
}