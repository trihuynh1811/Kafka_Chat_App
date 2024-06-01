package com.kafka.ChatApp;

import com.fasterxml.jackson.databind.JsonNode;
import com.kafka.ChatApp.Entity.Contact;
import com.kafka.ChatApp.Entity.Conversation;
import com.kafka.ChatApp.Entity.GroupMember;
import com.kafka.ChatApp.Entity.Message_;
import com.kafka.ChatApp.Repository.AccountRepo;
import com.kafka.ChatApp.Repository.ConversationRepo;
import com.kafka.ChatApp.Repository.GroupMemberRepo;
import com.kafka.ChatApp.Repository.MessageRepo;
import com.kafka.ChatApp.Services.Kafka.KafkaServices;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;


@RestController
public class ChatController {

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;
    @Autowired
    private KafkaServices kafkaServices;
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    ConversationRepo conversationRepo;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    GroupMemberRepo groupMemberRepo;

    String topic;
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @GetMapping(value = "/api/getListOfGroup", produces = "application/json")
    public List<String> getListOfGroup() throws ExecutionException, InterruptedException {
        List<ConsumerGroupListing> groupList = kafkaServices.getConsumerGroups();
        List<String> groupIdList = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            groupIdList.add(groupList.get(i).groupId());
        }
        return groupIdList;
    }

    @PostMapping(value = "/api/getListOfTopic", produces = "application/json")
    public ResponseEntity<?> getListOfTopic(@RequestBody JsonNode request) throws ExecutionException, InterruptedException {
        String username = request.get("username").asText();
        System.out.println(username);
        List<GroupMember> topicList = groupMemberRepo.findByContact(accountRepo.findById(username).get());

        for(int i = 0; i < topicList.size(); i++){
            Message_ latestMsg = messageRepo.findFirstByTopicOrderByTimestampDesc(topicList.get(i).getTopic());
            topicList.get(i).setLatestMsg(latestMsg);
        }
        System.out.println(topicList);
//        System.out.println(kafkaServices.getTopicList());
        System.out.println(topicList);
        return ResponseEntity.status(HttpStatus.OK).body(topicList);
    }

    @PostMapping(value = "/api/send", consumes = "application/json", produces = "application/json")
    public void sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
        try {
            //Sending the message to kafka topic queue
            String wsDest = "/topic/" + message.getTopic();
            String[] getReceiver = message.getTopic().split("_");
            System.out.println(getReceiver[0]);
            //Create new convo and group for new dm and save new message
            if (message.getTopicId() <= 0) {
                System.out.println("new message");
                Conversation conversation = Conversation.builder()
                        .id(generateIdForIndividualChat())
                        .conversationName(message.getTopic())
                        .build();

                conversationRepo.save(conversation);
                Message_ message_ = Message_.builder()
                        .content(message.getContent())
                        .topic(conversation)
                        .contact(accountRepo.findById(message.getSender()).get())
                        .timestamp(LocalDateTime.parse(message.getTimestamp()))
                        .replyToMessage(null)
                        .build();

                messageRepo.save(message_);
                for (String member : getReceiver) {
                    Contact contact = accountRepo.findById(member).get();
                    GroupMember groupMember = GroupMember.builder()
                            .topic(conversation)
                            .joinDate(LocalDateTime.now())
                            .contact(contact)
                            .build();
                    groupMemberRepo.save(groupMember);
                }
                message.setTopicId(conversation.getId());
                System.out.println("send to /topic/" + getReceiver[0]);
                kafkaTemplate.send(message.getTopic(), message).get();
//                template.convertAndSend(wsDest, message);
                template.convertAndSend(wsDest, message_);
//                template.convertAndSend("/topic/" + getReceiver[0], message);
                template.convertAndSend("/topic/" + getReceiver[0], message_);
                return;
            }

            // Inserting new message when there new message
            Conversation conversation = Conversation.builder()
                    .conversationName(message.getTopic())
                    .id(message.getTopicId())
                    .build();

            Message_ message_ = message.getReplyMsgId() > 0 ? 
                    Message_.builder()
                    .timestamp(LocalDateTime.parse(message.getTimestamp()))
                    .contact(accountRepo.findById(message.getSender()).get())
                    .topic(conversation)
                    .content(message.getContent())
                    .replyToMessage(messageRepo.findById(message.getReplyMsgId()).get())
                    .build() 
                    : 
                    Message_.builder()
                    .timestamp(LocalDateTime.parse(message.getTimestamp()))
                    .contact(accountRepo.findById(message.getSender()).get())
                    .topic(conversation)
                    .content(message.getContent())
                    .build();

            messageRepo.save(message_);
            System.out.println(message);
            System.out.println(accountRepo.findById(message.getSender()).get());
            topic = message.getTopic();
            kafkaTemplate.send(message.getTopic(), message).get();
            System.out.println("sent to " + wsDest);
//            template.convertAndSend(wsDest, message);
            template.convertAndSend(wsDest, message_);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public Message broadcastGroupMessage(@Payload Message message) {
        //Sending this message to all the subscribers
        System.out.println("ws" + message);
        return message;
    }

    public Long generateIdForIndividualChat() {
        try {
            Random random = new Random();

            // Generate a sequence of 15 random digits
            StringBuilder sequence = new StringBuilder();
            int messageIndicator = 1;

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
