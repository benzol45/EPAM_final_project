package com.benzol45.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    //TODO Выдача сообщений с ошибками валидации на нужных языках
    @NotNull
    @NotBlank
    @Size(min = 10, max = 13, message = "ISBN can be only 10 or 13 digits")
    @Pattern(regexp = "[0-9]*", message = "ISBN can contents only digits")
    private String ISBN;

    //TODO Прописать всем правила валидации
    private String author;
    private String title;
    private int pages;
    private String imagePath;
    private String publisher;
    private LocalDate dateOfPublication;
    private int quantity;
}
