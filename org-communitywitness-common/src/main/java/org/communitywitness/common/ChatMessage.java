package org.communitywitness.common;

import java.time.LocalDateTime;

public class ChatMessage {
    private int id = SpecialIds.UNSET_ID;
    private int reportId;
    private int investigatorId;
    private String message;
    private LocalDateTime timestamp;

    /**
     * 0-parameter constructor so that frameworks that work with beans
     * can work with this type of object (like Jersey and GSON).
     */
    public ChatMessage() {}

    /**
     * A constructor that sets all member variables at once for convenience.
     * @param id
     * @param reportId
     * @param investigatorId
     * @param message
     * @param timestamp
     */
    public ChatMessage(int id, int reportId, int investigatorId, String message, LocalDateTime timestamp) {
        this.id = id;
        this.reportId = reportId;
        this.investigatorId = investigatorId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(int investigatorId) {
        this.investigatorId = investigatorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
