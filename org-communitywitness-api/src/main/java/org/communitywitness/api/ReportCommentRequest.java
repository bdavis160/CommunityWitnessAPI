package org.communitywitness.api;

public class ReportCommentRequest {
    private int reportId;
    private int investigatorId;
    private String contents;

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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
