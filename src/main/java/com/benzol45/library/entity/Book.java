package com.benzol45.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Book
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 13, message = "ISBN can be only 10 or 13 digits")
    @Pattern(regexp = "[0-9]*", message = "ISBN can contents only digits")
    private String ISBN;

    @NotNull
    @NotBlank
    private String author;

    @NotNull
    @NotBlank
    private String title;

    @Positive
    private int pages;

    private String imagePath;

    @NotNull
    @NotBlank
    private String publisher;

    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfPublication;

    @Positive
    private int quantity;

    private double rating;
}
