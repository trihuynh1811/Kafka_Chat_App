package com.kafka.ChatApp;

import com.kafka.ChatApp.Repository.ConversationRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class crudDB {

    @Autowired
    ConversationRepo conversationRepo;

    @Test
    public void deleteConversation(){
        conversationRepo.deleteAll();
    }
}
