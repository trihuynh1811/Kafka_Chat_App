package com.kafka.ChatApp.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.kafka.ChatApp.Entity.Conversation;
import com.kafka.ChatApp.Entity.Message_;
import com.kafka.ChatApp.Repository.ConversationRepo;
import com.kafka.ChatApp.Repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RestController
public class MessageApi {

    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    ConversationRepo conversationRepo;

    @PostMapping("/api/message/get")
    public ResponseEntity<?> listOfMessage(@RequestBody JsonNode request){
        Long topicId = request.get("topicId").asLong();
        Conversation topic = conversationRepo.findById(topicId).get();
        List<Message_> messageList = reverseMessageList(messageRepo.findTop10ByTopicOrderByTimestampDesc(topic));
        System.out.println(messageList);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    @PostMapping("/api/message-from-timestamp/get")
    public ResponseEntity<?> listOfMessageFromTimeStamp(@RequestBody JsonNode request){
        Long topicId = request.get("topicId").asLong();
        String timeStampString = request.get("timeStamp").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");
        LocalDateTime timestamp = LocalDateTime.parse(timeStampString, formatter);
        Conversation topic = conversationRepo.findById(topicId).get();
        List<Message_> messageList = reverseMessageList(messageRepo.findTop10ByTimestampLessThanAndTopicOrderByTimestampDesc(timestamp, topic));
        System.out.println(messageList);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    @PostMapping("/api/message-between-timestamp/get")
    public ResponseEntity<?> listOfMessageBetweenTimeStamp(@RequestBody JsonNode request) {
        Long topicId = request.get("topicId").asLong();
        String timeStampString = request.get("timeStamp").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");
        LocalDateTime timestamp = LocalDateTime.parse(timeStampString, formatter);
        Conversation topic = conversationRepo.findById(topicId).get();
        List<Message_> messageListLessThanEqualTimeStamp = reverseMessageList(messageRepo.findTop10ByTimestampLessThanAndTopicOrderByTimestampDesc(timestamp, topic));
        List<Message_> messageListGreaterThanEqualTimeStamp = messageRepo.findTop10ByTimestampGreaterThanEqualAndTopicOrderByTimestampAsc(timestamp, topic);
        List<Message_> messageList = Stream.concat(messageListLessThanEqualTimeStamp.stream(), messageListGreaterThanEqualTimeStamp.stream()).toList();
        System.out.println("---------------------------------less than-------------------------------------");
        System.out.println(messageListLessThanEqualTimeStamp);
        System.out.println("---------------------------------less than-------------------------------------");
        System.out.println("---------------------------------greater than-------------------------------------");
        System.out.println(messageListGreaterThanEqualTimeStamp);
        System.out.println("---------------------------------greater than-------------------------------------");
        System.out.println(messageList);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    @PostMapping("/api/message-after-timestamp/get")
    public ResponseEntity<?> listOfMessageAfterTimeStamp(@RequestBody JsonNode request){
        Long topicId = request.get("topicId").asLong();
        String timeStampString = request.get("timeStamp").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");
        LocalDateTime timestamp = LocalDateTime.parse(timeStampString, formatter);
        Conversation topic = conversationRepo.findById(topicId).get();
        List<Message_> messageList = messageRepo.findTop10ByTimestampGreaterThanAndTopicOrderByTimestampAsc(timestamp, topic);
        System.out.println(messageList);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    @PostMapping("/api/message/delete")
    public ResponseEntity<?> deleteMessage(@RequestBody JsonNode request){
        Long messageId = request.get("messageId").asLong();
        Long topicId = request.get("topicId").asLong();
        String conversationName = conversationRepo.findById(topicId).get().getConversationName();
        String wsDest = "/topic/" + conversationName;
        String deleteTxt = "message deleted";
        Message_ message = messageRepo.findById(messageId).get();

        message.setDeleted(true);
        message.setHasBeenEdited(true);
        System.out.println(message);
        messageRepo.save(message);
        template.convertAndSend(wsDest, message);
        return ResponseEntity.status(HttpStatus.OK).body("delete message with id " + messageId);
    }

    @PostMapping("/api/message/edit")
    public ResponseEntity<?> editMessage(@RequestBody JsonNode request){
        Long topicId = request.get("topicId").asLong();
        Long messageId = request.get("messageId").asLong();
        String editedMsgContent = request.get("content").asText();
        String conversationName = conversationRepo.findById(topicId).get().getConversationName();
        String wsDest = "/topic/" + conversationName;
        Message_ message = messageRepo.findById(messageId).get();

        message.setContent(editedMsgContent);
        message.setEdited(true);
        message.setHasBeenEdited(true);
        messageRepo.save(message);
        template.convertAndSend(wsDest, message);
        return ResponseEntity.status(HttpStatus.OK).body("edited message with id: " + messageId);
    }

    List<Message_> reverseMessageList(List<Message_> msgList){
        int left = 0;
        int right = msgList.size() - 1;

        while (left < right) {
            // Swap elements at left and right indices
            Message_ temp = msgList.get(left);
            msgList.set(left, msgList.get(right));
            msgList.set(right, temp);

            // Move the indices towards the center
            left++;
            right--;
        }
        return msgList;
    }
    
}
