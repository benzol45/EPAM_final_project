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
        Optional<Book> optionalBook = isbnService.fillByISBN("5352003523");

        assertTrue(optionalBook.isPresent());
        Book book = optionalBook.get();
        assertEquals("9785352003527", book.getISBN());
        assertEquals("D. Tolkin", book.getAuthor());
        assertEquals("Vlastelin Kolets", book.getTitle());
        assertEquals("Azbuka", book.getPublisher());
        assertEquals(LocalDate.of(2005,4,8), book.getDateOfPublication());
    }
}

//    void fillByISBN() throws IOException {
//        ISBNservice isbNservice = new ISBNservice(new RestTemplate());
//        List<Optional<Book>> collect = Files.lines(Path.of("./src/main/resources/isbn_base_books_list.txt").toAbsolutePath()).distinct().map(line -> isbNservice.fillByISBN(line.trim())).collect(Collectors.toList());
//        List<Book> collect1 = collect.stream().map(o -> o.get()).map(o->{Random random = new Random(); o.setQuantity(random.nextInt(5)+1); return o;}).map(o -> bookRepository.save(o)).collect(Collectors.toList());
//        System.out.println(collect1);
//    }