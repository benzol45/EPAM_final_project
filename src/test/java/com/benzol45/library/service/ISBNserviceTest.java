package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ISBNserviceTest {
    private final BookRepository bookRepository;

    @Autowired
    public ISBNserviceTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void fillByISBN() throws IOException {
        ISBNservice isbNservice = new ISBNservice(new RestTemplate());
        List<Optional<Book>> collect = Files.lines(Path.of("./src/main/resources/isbn_base_books_list.txt").toAbsolutePath()).distinct().map(line -> isbNservice.fillByISBN(line.trim())).collect(Collectors.toList());
        List<Book> collect1 = collect.stream().map(o -> o.get()).map(o->{Random random = new Random(); o.setQuantity(random.nextInt(5)+1); return o;}).map(o -> bookRepository.save(o)).collect(Collectors.toList());
        System.out.println(collect1);
    }
}