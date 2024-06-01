package com.kafka.ChatApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "email_unique_constraint",
                        columnNames = "email"
                )
        }
)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String displayName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

     @Basic
     @Temporal(TemporalType.TIMESTAMP)
     private LocalDateTime loginTimeStamp;
    
     @Basic
     @Temporal(TemporalType.TIMESTAMP)
     private LocalDateTime logoutTimeStamp;


}
