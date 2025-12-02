package com.example.finalprojectmp.utils;

public enum EventStatus {
    PAST("Past"),
    CURRENT("Today"),
    UPCOMING("Upcoming");

    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
