package com.benzol45.library.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//TODO РЕАЛИЗОВАТЬ !!! + сущность штрафы (пользователь-сумма-дата)
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    private LocalDateTime createDate;
}
