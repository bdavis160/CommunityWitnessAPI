package org.communitywitness.api;

import org.junit.jupiter.api.Test;

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
}