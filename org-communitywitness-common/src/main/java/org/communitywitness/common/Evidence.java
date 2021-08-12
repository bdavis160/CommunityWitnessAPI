package org.communitywitness.common;

import java.time.LocalDateTime;

/**
 * A class representing the information in the database about a single piece of evidence.
 * Where a piece of evidence is some sort of media (video, picture, sound, etc...) 
 * along with some metadata about it.
 */
public class Evidence {
	private int id = SpecialIds.UNSET_ID;
	private String title;
	private String type;
	private LocalDateTime timestamp;
	private String link;
	private int reportId;

	/**
	 * 0-parameter constructor so that frameworks that work with beans 
	 * can work with this type of object (like Jersey and GSON).
	 */
	public Evidence() {}

	// TODO: Maybe we should remove this. Setting id manually is dangerous.
	/**
	 * A constructor that sets all member variables at once for convenience.
	 * @param id see {@link #setId(int)}
	 * @param title see {@link #setTitle(String)}
	 * @param type see {@link #setType(String)}
	 * @param timestamp see {@link #setTimestamp(LocalDateTime)}
	 * @param link see {@link #setLink(String)}
	 * @param reportId see {@link #setReportId(int)}
	 */
	public Evidence(int id, String title, String type, LocalDateTime timestamp, String link, int reportId) {
		setId(id);
		setTitle(title);
		setType(type);
		setTimestamp(timestamp);
		setLink(link);
		setReportId(reportId);
	}

	/**
	 * Returns the database's id number of this evidence.
	 * @return this.id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this evidence.
	 * @param id the id number associated with this evidence
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the descriptive title of this evidence.
	 * @return this.title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title for this evidence.
	 * @param title a descriptive title for this evidence
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the type/format of this evidence, for example video or sound.
	 * @return this.type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of content this evidence contains.
	 * @param type the type/format this evidence is in, like video or sound
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the timestamp of the creation of this evidence
	 * @return this.timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp for this evidence.
	 * @param timestamp the date and time that this evidence was originally created
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the location of the file containing this evidence.
	 * @return this.link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the link for this evidence.
	 * @param link the location of the file containing this evidence
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Returns the id of the report this evidence is associated with.
	 * @return this.reportId
	 */
	public int getReportId() {
		return reportId;
	}

	/**
	 * Sets the report id of this evidence.
	 * @param reportId the id of the report this evidence is associated with
	 */
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}
