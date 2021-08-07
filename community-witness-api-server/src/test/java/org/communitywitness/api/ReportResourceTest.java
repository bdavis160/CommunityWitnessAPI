package org.communitywitness.api;

import org.communitywitness.api.Report;
import org.communitywitness.api.ReportResource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

class ReportResourceTest {
    ReportResource res = new ReportResource();

    @Test
    void queryReports() throws SQLException {
        List<Report> myList = res.queryReports("test location", "2021-07-29 18:08:05.0");
        assertNotNull(myList);
    }

    @Test
    void createReportTest() throws SQLException {
        Date time = new Date();
        int myId = res.createReport("from unit test", time, "test location");
        assertNotEquals(-1, myId);
    }

    @Test
    void getReport() {
        int testReportId = 2;
        Report testReport = res.getReport(testReportId);
        assertNotNull(testReport);
    }

    @Test
    void updateReportStatus() {
        int testReportId = 2;
        Report testReport = res.getReport(testReportId);
        boolean newStatus = !testReport.getResolved();
        res.updateReportStatus(2, newStatus);
        assertEquals(newStatus, res.getReport(testReportId).getResolved());
    }
}
