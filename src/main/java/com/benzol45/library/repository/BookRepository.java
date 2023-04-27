package com.benzol45.library.repository;

import com.benzol45.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByISBN(String ISBN);
    Page<Book> findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(String requestTitle, String requestAuthor, Pageable pageable);
}
