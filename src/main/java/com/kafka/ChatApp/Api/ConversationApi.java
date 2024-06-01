package com.kafka.ChatApp.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.kafka.ChatApp.Entity.Contact;
import com.kafka.ChatApp.Entity.Conversation;
import com.kafka.ChatApp.Entity.GroupMember;
import com.kafka.ChatApp.Repository.AccountRepo;
import com.kafka.ChatApp.Repository.ConversationRepo;
import com.kafka.ChatApp.Repository.GroupMemberRepo;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
public class ConversationApi {

    @Autowired
    ConversationRepo conversationRepo;
    @Autowired
    GroupMemberRepo groupMemberRepo;
    @Autowired
    AccountRepo accountRepo;

    @PostMapping("/api/conversation/create")
    public ResponseEntity<?> createNewConversation(@RequestBody JsonNode request) {
        String conversationName = request.get("groupName").asText();
        String username = request.get("username").asText();
        Contact contact = accountRepo.findById(username).get();

        Conversation conversation = Conversation.builder()
                .id(ConversationIdGenerator())
                .conversationName(conversationName)
                .build();

        GroupMember groupMember = GroupMember.builder()
                .topic(conversation)
                .joinDate(LocalDateTime.now())
                .contact(contact)
                .build();

        conversationRepo.save(conversation);
        groupMemberRepo.save(groupMember);

        return ResponseEntity.status(HttpStatus.OK).body(conversation);
    }

    public Long ConversationIdGenerator() {
        try {
            Random random = new Random();

            // Generate a sequence of 15 random digits
            StringBuilder sequence = new StringBuilder();
            int messageIndicator = 2;

            for (int i = 0; i < 15; i++) {
                int randomDigit = random.nextInt(10); // Generates random number between 0 and 9 (inclusive)
                sequence.append(randomDigit);
            }

            String finalMessageId = Integer.toString(messageIndicator) + sequence.toString();
            System.out.println(Long.parseLong(finalMessageId));
            return Long.parseLong(finalMessageId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


