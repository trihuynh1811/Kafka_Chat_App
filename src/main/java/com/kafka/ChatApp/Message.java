package com.kafka.ChatApp;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Message {
    private String sender;
    private String content;
    private String topic;
    private String timestamp;
    private Long topicId;
    private Long replyMsgId;
    private String replyMsgContent;

    public Message(String sender, String content, String topic, Long topicId, Long replyMsgId, String replyMsgContent) {
        this.sender = sender;
        this.content = content;
        this.topic = topic;
        this.topicId = topicId;
        this.replyMsgId = replyMsgId;
        this.replyMsgContent = replyMsgContent;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", topic='" + topic + '\'' +
                ", topicId='" + topicId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", replyMsgId='" + replyMsgId + '\'' +
                ", replyMsgContent='" + replyMsgContent + '\'' +
                '}';
    }
}
