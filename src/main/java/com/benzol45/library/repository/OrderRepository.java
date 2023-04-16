package com.benzol45.library.repository;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId, Sort sort);
    Optional<Order> findByBookAndUser(Book book, User user);
}
