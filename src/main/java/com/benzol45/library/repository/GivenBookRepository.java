package com.benzol45.library.repository;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GivenBookRepository extends JpaRepository<GivenBook, Long> {
    int countAllByBook(Book book);
}
