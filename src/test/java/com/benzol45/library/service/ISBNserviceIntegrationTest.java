package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class ISBNserviceIntegrationTest {

    @Test
    void fillByISBN() {
        ISBNservice isbnService = new ISBNservice(new RestTemplate());
        Optional<Book> optionalBook = isbnService.fillByISBN("9780544003415");

        assertTrue(optionalBook.isPresent());
        Book book = optionalBook.get();
        assertEquals("9780544003415", book.getISBN());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals("The lord of the rings", book.getTitle());
        assertEquals(1216, book.getPages());
        assertEquals("Houghton Mifflin", book.getPublisher());
        assertEquals(LocalDate.of(2012,12,31), book.getDateOfPublication());
    }
}