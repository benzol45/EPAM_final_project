package com.benzol45.library.repository;

import com.benzol45.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByISBN(String ISBN);
    Page<Book> findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(String requestTitle, String requestAuthor, Pageable pageable);
    @Query("SELECT SUM(b.quantity) FROM Book b")
    Integer countBookCopy();
}
