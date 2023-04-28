package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RatingServiceIntegrationTest {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private BookService bookService;

    @Test
    void getAverageRatingByBook() {
        //сложить туда несколько книг от нескольких юзеров, часть не оценены ещё и часть оценены, проверить на целый и дробный рейтинг
        Book book = bookService.getById(1L);
        assertEquals(4.3 ,ratingService.getAverageRatingByBook(book));
    }
}