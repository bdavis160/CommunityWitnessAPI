package org.communitywitness.api;

import org.communitywitness.common.ReportComment;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportCommentResourceTest {
    ReportCommentResource res = new ReportCommentResource();
    AuthenticatedUser investigator = new AuthenticatedUser("VHmFNLZCgL0A2to64A1cFLRE0PJuFBi+");

    ReportCommentResourceTest() throws BadLoginException {
    }

    @Test
    void createReportComment() throws SQLException {
        ReportCommentRequest reportCommentRequest = new ReportCommentRequest();
        reportCommentRequest.setReportId(0);
        reportCommentRequest.setInvestigatorId(0);
        reportCommentRequest.setContents("comment from createReportComment unit test");

        int myId = res.createReportComment(reportCommentRequest, investigator);
        assertNotEquals(-1, myId);
    }

    @Test
    void getReportComment() {
        int id = 0;
        ReportComment comment = res.getReportComment(id, investigator);
        assertNotNull(comment);
    }
}