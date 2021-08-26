package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InvestigatorResourceTest {
    InvestigatorResource res = new InvestigatorResource();

    @Test
    void createInvestigator() throws SQLException {
        NewInvestigatorRequest newInvestigatorRequestData = new NewInvestigatorRequest();
        newInvestigatorRequestData.setName("name from unit test");
        newInvestigatorRequestData.setOrganization("organization from unit test");
        newInvestigatorRequestData.setOrganizationType("organization type from unit test");

        int myId = res.createInvestigator(newInvestigatorRequestData);
        assertNotEquals(-1, myId);
    }

    @Test
    void getInvestigator() {
        int id = 2;
        Investigator investigator = res.getInvestigator(id);
        assertNotNull(investigator);
    }

    @Test
    void updateInvestigator() {
        int investigatorId = 2;
        Investigator oldInvestigator = res.getInvestigator(investigatorId);
        UpdateInvestigatorRequest oldInvestigatorData = new UpdateInvestigatorRequest();
        oldInvestigatorData.setName(oldInvestigator.getName());
        oldInvestigatorData.setOrganization(oldInvestigator.getOrganization());
        oldInvestigatorData.setOrganizationType(oldInvestigator.getOrganizationType());

        UpdateInvestigatorRequest newInvestigator = new UpdateInvestigatorRequest();
        newInvestigator.setName("updateInvestigatorTestName");
        newInvestigator.setOrganization("updateInvestigatorTestOrganization");
        newInvestigator.setOrganizationType("updateInvestigatorTestOrganizationType");

        res.updateInvestigator(2, newInvestigator);
        assertEquals(newInvestigator.getName(), res.getInvestigator(investigatorId).getName());
        assertEquals(newInvestigator.getOrganization(), res.getInvestigator(investigatorId).getOrganization());
        assertEquals(newInvestigator.getOrganizationType(), res.getInvestigator(investigatorId).getOrganizationType());

        res.updateInvestigator(2, oldInvestigatorData);
    }

    @Test
    void takeCaseValidIds() throws SQLException {
        int reportId = 0;
        int investigatorId = 0;
        assertEquals(200, res.takeCase(investigatorId, reportId).getStatus());

        // in order to make this test succeed more than once, we need to delete the test row after it completes
        Connection conn = new SQLConnection().databaseConnection();

        String query = "DELETE FROM reportinvestigations WHERE reportid=? AND investigatorid=? ";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, reportId);
        queryStatement.setInt(2, investigatorId);

        queryStatement.executeUpdate();
        queryStatement.close();
    }

    @Test
    void takeCaseInvalidReportId() throws SQLException {
        int reportId = 1;
        int investigatorId = 0;
        assertEquals(404, res.takeCase(investigatorId, reportId).getStatus());
    }

    @Test
    void takeCaseInvalidInvestigatorId() throws SQLException {
        int reportId = 0;
        int investigatorId = 1000;
        assertEquals(404, res.takeCase(investigatorId, reportId).getStatus());
    }

    @Test
    void takeCaseInvalidIds() throws SQLException {
        int reportId = 1;
        int investigatorId = 1000;
        assertEquals(404, res.takeCase(investigatorId, reportId).getStatus());

    }
}