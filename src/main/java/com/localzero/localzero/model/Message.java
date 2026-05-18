package com.localzero.localzero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String senderId;

    private String receiverId;

    private String content;

    private LocalDateTime timestamp;

    private boolean delivered = false;

    @Transient
    private String receiverEmail;
}
