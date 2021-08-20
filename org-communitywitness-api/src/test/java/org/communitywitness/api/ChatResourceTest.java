package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

class ChatResourceTest {
    ChatResource chatResource = new ChatResource();

    @Test
    void investigatorMessages() throws SQLException {
        List<ChatMessage> list = chatResource.investigatorMessages(0);
        assert(list.size() > 1);
    }

    @Test
    void witnessMessages() throws SQLException {
        List<ChatMessage> list = chatResource.witnessMessages(48);
        assert(list.size() > 1);
    }

    @Test
    void addMessage() throws SQLException {
        ChatMessageRequest req = new ChatMessageRequest();
        req.setInvestigatorId(1);
        req.setMessage("from addmessage unit test");
        req.setTime(LocalDateTime.now());

        assert(chatResource.addMessage(3, req) != -1);
    }
}