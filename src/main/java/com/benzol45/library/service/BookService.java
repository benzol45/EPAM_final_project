package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAll() {
        //TODO Сортировка и пагинация. Размер старницы - в пропертис и внедрять через @ConfigurationProperties
        //https://www.baeldung.com/spring-data-jpa-pagination-sorting
        Pageable pageable = PageRequest.of(0,10, Sort.by("title"));
        return bookRepository.findAll(pageable).getContent();
    }
}
