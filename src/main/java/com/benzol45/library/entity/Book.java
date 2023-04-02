package com.benzol45.library.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "book")
public class Book {
    //TODO Нужны ли нам авторы и издетели отдельными таблицами ? нужно ли количество экземпляров книг отдельной таблицей ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ISBN;
    private String author;
    private String title;
    private String publisher;
    private LocalDate dateOfPublication;
    private int quantity;
}
