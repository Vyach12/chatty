package ru.gusarov.messenger.models;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "date_of_sending", nullable = false)
    private LocalDateTime dateOfSending;

    @Column(name = "date_of_change")
    private LocalDateTime dateOfChange;
}
