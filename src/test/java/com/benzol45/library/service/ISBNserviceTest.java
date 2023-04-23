package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ISBNserviceTest {

    @Test
    void fillByISBN() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Map<String,Object> bookResponse = Map.of("isbn_13", List.of("1234567890128"),
                                                 "authors", List.of(Map.of("key","/authors/mock_author")),
                                                 "title", "MockTitle",
                                                 "number_of_pages",42,
                                                 "publishers", List.of("MockPublisher"),
                                                 "publish_date","Jan 10, 2011");
        when(mockRestTemplate.getForEntity(contains("https://openlibrary.org/isbn"),any())).thenReturn(ResponseEntity.ok().body(bookResponse));
        when(mockRestTemplate.getForObject(contains("https://openlibrary.org/authors/mock_author"),any())).thenReturn(Map.of("name","MockAuthor"));

        ISBNservice isbnService = new ISBNservice(mockRestTemplate);
        Optional<Book> optionalBook = isbnService.fillByISBN("1234567890128");

        assertTrue(optionalBook.isPresent());
        Book book = optionalBook.get();
        assertEquals("1234567890128", book.getISBN());
        assertEquals("MockAuthor", book.getAuthor());
        assertEquals("MockTitle", book.getTitle());
        assertEquals(42, book.getPages());
        assertEquals("MockPublisher", book.getPublisher());
        assertEquals(LocalDate.of(2011,1,10), book.getDateOfPublication());
    }

    @Test
    void getCoverImageMock() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Map<String,Object> bookResponse = Map.of("isbn_13", List.of("1234567890128"), "covers", List.of(12345));
        when(mockRestTemplate.getForEntity(contains("https://openlibrary.org/isbn"),any())).thenReturn(ResponseEntity.ok().body(bookResponse));

        ISBNservice isbnService = new ISBNservice(mockRestTemplate);
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg",isbnService.getCoverImageURL("9780544003415").get());
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg",isbnService.getCoverImageURL("978-0-5440-0341-5").get());
    }

    @Test
    void getCoverImageRealConnecting() {
        ISBNservice isbnService = new ISBNservice(new RestTemplate());
        assertTrue(isbnService.getCoverImageURL("978-5-9614-8320-8").isEmpty()); //нету
        assertTrue(isbnService.getCoverImageURL("978-5-9614-8320-8-999").isEmpty()); //нету, ошибочный
        assertTrue(isbnService.getCoverImageURL("978-0-5440-0341-5").isPresent()); //есть
        assertTrue(isbnService.getCoverImageURL("9780544003415").isPresent()); //есть
        assertEquals("https://covers.openlibrary.org/b/id/13911921-M.jpg",isbnService.getCoverImageURL("9780544003415").get());
    }
}