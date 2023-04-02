package com.benzol45.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String contact;

    private String login;
    private String password;
    private boolean isBlocked;
    @Enumerated(EnumType.STRING)
    private Role role;
    enum Role {
        READER, LIBRARIAN, ADMINISTRATOR
    }
}
