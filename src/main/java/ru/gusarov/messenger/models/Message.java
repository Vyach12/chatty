package ru.gusarov.messenger.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "person_id", nullable = false)
    private Integer personId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "date_of_sending", nullable = false)
    private LocalDateTime dateOfSending;

    @Column(name = "date_of_change")
    private LocalDateTime dateOfChange;
}
