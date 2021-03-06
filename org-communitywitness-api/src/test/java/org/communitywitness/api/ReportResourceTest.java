package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportResourceTest {
    ReportResource res = new ReportResource();
    AuthenticatedUser witness = new AuthenticatedUser("9XgUrSeCL9orXybssKknDW8vDJE2JkMS");

    ReportResourceTest() throws BadLoginException {
    }

    @Test
    void queryReports() throws SQLException {
        List<Report> myList = res.queryReports();
        assertNotNull(myList);
    }

    @Test
    void createReportTest() throws SQLException {
        LocalDateTime time = LocalDateTime.now();
        NewReportRequest newReportRequestData= new NewReportRequest();
        newReportRequestData.setDescription("description from unit test");
        newReportRequestData.setTime(time);
        newReportRequestData.setLocation("location from unit test");
        newReportRequestData.setWitnessId(1);

        int myId = res.createReport(newReportRequestData, witness);
        assertNotEquals(-1, myId);
    }

    @Test
    void getReport() {
        int testReportId = 183;
        Report testReport = res.getReport(testReportId, witness);
        assertNotNull(testReport);
    }

    @Test
    void updateReportStatus() {
        int testReportId = 183;
        Report testReport = res.getReport(testReportId, witness);
        boolean newStatus = !testReport.getResolved();
        res.updateReportStatus(183, newStatus, witness);
        assertEquals(newStatus, res.getReport(testReportId, witness).getResolved());
    }
}
