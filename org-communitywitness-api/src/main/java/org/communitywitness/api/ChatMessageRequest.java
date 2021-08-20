package org.communitywitness.api;

import java.time.LocalDateTime;

public class ChatMessageRequest {
    int investigatorId;
    String message;
    LocalDateTime time;

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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
