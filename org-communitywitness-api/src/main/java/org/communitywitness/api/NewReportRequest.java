package org.communitywitness.api;

import java.time.LocalDateTime;

public class NewReportRequest {
    private String description;
    private LocalDateTime time;
    private String location;
    private int witnessId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTime() { return time; }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWitnessId() {
        return witnessId;
    }

    public void setWitnessId(int witnessId) {
        this.witnessId = witnessId;
    }
}
