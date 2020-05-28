package com.nicktackes;

import java.time.LocalDate;

public class TimelineEvent {
    String title;
    LocalDate startDate;
    LocalDate endDate;
    String description;

    public TimelineEvent(String title, LocalDate startDate, LocalDate endDate, String description) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public boolean hasRange() {
        return !this.startDate.equals(this.endDate);
    }

    public boolean sameYear () {
        return this.startDate.getYear() == this.endDate.getYear();
    }

    public boolean sameMonth () {
        return this.startDate.getMonthValue() == this.endDate.getMonthValue() && sameYear();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate (LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate (LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public String getDescription() {
        return this.description;
    }
}