package com.benzol45.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private int pages;
    private String imagePath;
    private String publisher;
    private LocalDate dateOfPublication;
    private int quantity;
}
