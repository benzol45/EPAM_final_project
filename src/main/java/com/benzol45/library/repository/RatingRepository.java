package com.benzol45.library.repository;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Rating;
import com.benzol45.library.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findAllByUserAndRateIsNull(User user, Sort sort);

    @Query("SELECT SUM(r.rate) FROM Rating r WHERE (r.rate IS NOT NULL) AND (r.book=?1)")
    Integer sumRateByBook(Book book);

    @Query("SELECT COUNT(r) FROM Rating r WHERE (r.rate IS NOT NULL) AND (r.book=?1)")
    Integer countRateByBook(Book book);
}
