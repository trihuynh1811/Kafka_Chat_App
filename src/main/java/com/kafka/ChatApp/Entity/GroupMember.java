package com.kafka.ChatApp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "conversation_id",
            referencedColumnName = "conversation_id"
    )
    private Conversation topic;

    @ManyToOne
    @JoinColumn(
            name = "username",
            referencedColumnName = "username"
    )
    private Contact contact;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime joinDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime leftDate;

    @Transient
    private Message_ latestMsg;

}
