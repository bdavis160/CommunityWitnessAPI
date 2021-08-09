package org.communitywitness.common;

import java.util.Date;
import java.util.List;

/**
 * A class representing the information in the database about a report.
 * Where a report is a user-submitted report about an event they find concerning
 * or otherwise think is worthy of investigation.
 */
public class Report {
    private int id = SpecialIds.UNSET_ID;
    private boolean resolved;
    private String description;
    private Date timestamp;
    private String location;
    private int witnessId;
    private List<Integer> comments;
    private List<Integer> evidence;

    /**
	 * 0-parameter constructor so that frameworks that work with beans 
	 * can work with this type of object (like Jersey and GSON).
	 */
    public Report() {}
    
    /**
     * A constructor that sets all member variables at once for convenience.
     * @param id see {@link #setId(int)}
     * @param resolved see {@link #setResolved(boolean)}
     * @param description see {@link #setDescription(String)}
     * @param timestamp see {@link #setTimestamp(Date)}
     * @param location see {@link #setLocation(String)}
     * @param witnessId see {@link #setWitnessId(int)}
     * @param comments see {@link #setComments(List)}
     * @param evidence see {@link #setEvidence(List)}
     */
    public Report(int id, boolean resolved, String description, Date timestamp, 
    		String location, int witnessId, List<Integer> comments, List<Integer> evidence) {
    	setId(id);
    	setResolved(resolved);
    	setDescription(description);
    	setTimestamp(timestamp);
    	setLocation(location);
    	setWitnessId(witnessId);
    	setComments(comments);
    	setEvidence(evidence);
    }

    /**
     * Returns the database's id number of this report.
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this report.
     * @param id the id number associated with this report
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns a boolean indicating whether the report is solved (true) or not (false).
     * @return this.resolved
     */
    public boolean getResolved() {
        return resolved;
    }

    /**
     * Sets whether this report is resolved or not.
     * @param resolved true if this report is resolved, false if it isn't
     */
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Returns a description of the event this report is about.
     * @return this.description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this report.
     * @param description a description of the event this report is about
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date and time that the event this report concerns occurred.
     * @return this.timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of this report.
     * @param timestamp the date and time that the event this report concerns occurred
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the location that the event this report is concerned about occurred at.
     * @return this.location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of this report.
     * @param location the location that the event this report concerns occurred at
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the id of the witness that filed this report.
     * @return this.witnessId
     */
    public int getWitnessId() {
        return witnessId;
    }

    /**
     * Sets the id of the witness of this report.
     * @param witnessId the id number of the witness that filed this report
     */
    public void setWitnessId(int witnessId) {
        this.witnessId = witnessId;
    }

    /**
     * Returns a list of the id numbers of the comments on this report.
     * @return this.comments
     */
	public List<Integer> getComments() {
		return comments;
	}

	/**
	 * Sets the ids of the comments on this report.
	 * @param comments a list of id numbers of comments on this report
	 */
	public void setComments(List<Integer> comments) {
		this.comments = comments;
	}

	/**
	 * Returns a list of the id numbers of evidence for this report.
	 * @return this.evidence
	 */
	public List<Integer> getEvidence() {
		return evidence;
	}

	/**
	 * Sets the ids of the evidence for this report.
	 * @param evidence a list of id numbers of evidence for this report
	 */
	public void setEvidence(List<Integer> evidence) {
		this.evidence = evidence;
	}
}
