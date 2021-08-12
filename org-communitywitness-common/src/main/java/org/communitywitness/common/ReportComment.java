package org.communitywitness.common;

/**
 * A class representing the information in the database about a comment on a report.
 * Where a comment on a report, or a ReportComment, 
 * is a text comment left on a report by an investigator.
 */
public class ReportComment {
	private int id = SpecialIds.UNSET_ID;
	private int reportId;
	private int investigatorId;
	private String contents;
	
	/**
	 * 0-parameter constructor so that frameworks that work with beans 
	 * can work with this type of object (like Jersey and GSON).
	 */
	public ReportComment() {}

	// TODO: I think we maybe should remove this, setting id manually is dangerous.
	/**
	 * A constructor that sets all member variables at once for convenience.
	 * @param id see {@link #setId(int)}
	 * @param reportId see {@link #setReportId(int)}
	 * @param investigatorId see {@link #setInvestigatorId(int)}
	 * @param contents see {@link #setContents(String)}
	 */
	public ReportComment(int id, int reportId, int investigatorId, String contents) {
		setId(id);
		setReportId(reportId);
		setInvestigatorId(investigatorId);
		setContents(contents);
	}
	
	/**
	 * Returns the database's id number of this comment
	 * @return this.id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id of this comment.
	 * @param id the id number associated with this comment
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the id number of the report this comment is associated with.
	 * @return this.reportId
	 */
	public int getReportId() {
		return reportId;
	}
	
	/**
	 * Sets the id of the report this comment is associated with.
	 * @param reportID the id number of the report this comment is associated with
	 */
	public void setReportId(int reportID) {
		this.reportId = reportID;
	}
	
	/**
	 * Returns the id number of the investigator that wrote this comment.
	 * @return this.investigatorId
	 */
	public int getInvestigatorId() {
		return investigatorId;
	}
	
	/**
	 * Sets the id of the investigator that wrote this comment.
	 * @param investigatorID the id number of the investigator that wrote this comment
	 */
	public void setInvestigatorId(int investigatorID) {
		this.investigatorId = investigatorID;
	}
	
	/**
	 * Returns the text of this comment.
	 * @return this.contents
	 */
	public String getContents() {
		return contents;
	}
	
	/**
	 * Sets the contents of this comment.
	 * @param contents the actual text of this comment
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
}
