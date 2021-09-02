package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

class ChatResourceTest {
    ChatResource chatResource = new ChatResource();
    AuthenticatedUser witness = new AuthenticatedUser("9XgUrSeCL9orXybssKknDW8vDJE2JkMS");
    AuthenticatedUser investigator = new AuthenticatedUser("dYOsflWZANe6aD2Piil94041GCgi8Qsu");

    ChatResourceTest() throws BadLoginException {
    }

    @Test
    void investigatorMessages() throws SQLException {
        List<ChatMessage> list = chatResource.investigatorMessages(1, investigator);
        assert(list.size() > 1);
    }

    @Test
    void witnessMessages() throws SQLException {
        List<ChatMessage> list = chatResource.witnessMessages(1, witness);
        assert(list.size() > 0);
    }

    @Test
    void addMessage() throws SQLException {
        ChatMessageRequest req = new ChatMessageRequest();
        req.setInvestigatorId(1);
        req.setMessage("from addmessage unit test");
        req.setTime(LocalDateTime.now());

        assert(chatResource.addMessage(3, req, investigator) != -1);
    }
}