package com.kafka.ChatApp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Message_ {

    @Id
    @GenericGenerator(name = "sequence_message_id", strategy = "com.kafka.ChatApp.Entity.CustomGenerator.MessageIdGenerator")
    @GeneratedValue(generator = "sequence_message_id")
    @Column(name = "message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "username",
            referencedColumnName = "username",
            nullable = false
    )
    private Contact contact;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "conversation_id",
            referencedColumnName = "conversation_id",
            nullable = false
    )
    private Conversation topic;

    @OneToOne(optional = true)
    private Message_ replyToMessage;

    private boolean deleted = false;
    private boolean edited = false;
    private boolean hasBeenEdited = false;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

}
