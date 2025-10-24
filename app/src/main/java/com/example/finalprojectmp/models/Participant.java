package com.example.finalprojectmp.models;
import java.util.ArrayList;
import java.util.List;

public class Participant extends User {

    // Participant-specific attributes
    private String interests;
    private String dietaryPreferences;
    private String emergencyContact;
    private String emergencyContactPhone;
    private int totalEventsAttended;
    private int totalRsvps;
    private float attendanceRate;
    private String preferredEventTypes;
    private boolean emailNotifications;
    private boolean smsNotifications;

    // Lists to store participant's activities
    private transient List<Rsvp> myRsvps;
    private transient List<Attendance> myAttendance;
    private transient List<QRCode> myQRCodes; // Changed from Qrcode to QRCode

    // Constructors
    public Participant() {
        super();
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.totalEventsAttended = 0;
        this.totalRsvps = 0;
        this.attendanceRate = 0.0f;
        initializeLists();
    }

    public Participant(String name, String email, String password, String phoneNumber) {
        super(name, email, password, phoneNumber);
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.totalEventsAttended = 0;
        this.totalRsvps = 0;
        this.attendanceRate = 0.0f;
        initializeLists();
    }

    public Participant(int id, String name, String email, String password, String phoneNumber) {
        super(id, name, email, password, phoneNumber);
        this.emailNotifications = true;
        this.smsNotifications = false;
        initializeLists();
    }

    private void initializeLists() {
        this.myRsvps = new ArrayList<>();
        this.myAttendance = new ArrayList<>();
        this.myQRCodes = new ArrayList<>(); // Changed from Qrcode to QRCode
    }

    // Implementation of abstract method
    @Override
    public String getUserType() {
        return "participant";
    }

    // Participant-specific methods
    public void updateAttendanceRate() {
        if (totalRsvps > 0) {
            this.attendanceRate = (float) totalEventsAttended / totalRsvps * 100;
        } else {
            this.attendanceRate = 0.0f;
        }
    }

    public boolean hasGoodAttendance() {
        return attendanceRate >= 75.0f;
    }

    public boolean isInterestedInEventType(String eventType) {
        if (preferredEventTypes == null || preferredEventTypes.isEmpty()) {
            return true;
        }
        return preferredEventTypes.toLowerCase().contains(eventType.toLowerCase());
    }

    public void addInterest(String interest) {
        if (interests == null || interests.isEmpty()) {
            interests = interest;
        } else if (!interests.contains(interest)) {
            interests += "," + interest;
        }
    }

    public List<String> getInterestsList() {
        List<String> interestsList = new ArrayList<>();
        if (interests != null && !interests.isEmpty()) {
            String[] interestsArray = interests.split(",");
            for (String interest : interestsArray) {
                interestsList.add(interest.trim());
            }
        }
        return interestsList;
    }

    public boolean hasDietaryRestrictions() {
        return dietaryPreferences != null && !dietaryPreferences.isEmpty();
    }

    // Getters and Setters
    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public int getTotalEventsAttended() {
        return totalEventsAttended;
    }

    public void setTotalEventsAttended(int totalEventsAttended) {
        this.totalEventsAttended = totalEventsAttended;
        updateAttendanceRate();
    }

    public int getTotalRsvps() {
        return totalRsvps;
    }

    public void setTotalRsvps(int totalRsvps) {
        this.totalRsvps = totalRsvps;
        updateAttendanceRate();
    }

    public float getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(float attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public String getPreferredEventTypes() {
        return preferredEventTypes;
    }

    public void setPreferredEventTypes(String preferredEventTypes) {
        this.preferredEventTypes = preferredEventTypes;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public List<Rsvp> getMyRsvps() {
        return myRsvps;
    }

    public void setMyRsvps(List<Rsvp> myRsvps) {
        this.myRsvps = myRsvps;
    }

    public List<Attendance> getMyAttendance() {
        return myAttendance;
    }

    public void setMyAttendance(List<Attendance> myAttendance) {
        this.myAttendance = myAttendance;
    }

    public List<QRCode> getMyQRCodes() { // Changed from Qrcode to QRCode
        return myQRCodes;
    }

    public void setMyQRCodes(List<QRCode> myQRCodes) { // Changed from Qrcode to QRCode
        this.myQRCodes = myQRCodes;
    }

    public void incrementEventsAttended() {
        this.totalEventsAttended++;
        updateAttendanceRate();
    }

    public void incrementRsvps() {
        this.totalRsvps++;
        updateAttendanceRate();
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", attendanceRate=" + attendanceRate + "%" +
                ", totalEvents=" + totalEventsAttended +
                ", preferences='" + preferredEventTypes + '\'' +
                '}';
    }
}