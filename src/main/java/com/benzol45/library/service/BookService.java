package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return bookRepository.findAll();
    }

    public List<Book> getListWithPaging(String filter, Pageable pageable) {
        if (filter==null || filter.isEmpty()) {
            return bookRepository.findAll(pageable).getContent();
        } else {
            return bookRepository.findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(filter,filter,pageable).getContent();
        }
    }

    public int getTotalPages(String filter, Pageable pageable) {
        if (filter==null || filter.isEmpty()) {
            return bookRepository.findAll(pageable).getTotalPages();
        } else {
            return bookRepository.findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(filter,filter,pageable).getTotalPages();
        }
    }

    public Book getById(Long id) {
        //TODO Не нашли - залогируй и верни пустое
        return bookRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Can't find book with id " + id));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
