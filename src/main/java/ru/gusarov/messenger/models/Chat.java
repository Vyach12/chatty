package ru.gusarov.messenger.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_person_id", nullable = false)
    private Integer firstPersonId;

    @Column(name = "second_person_id", nullable = false)
    private Integer secondPersonId;
}