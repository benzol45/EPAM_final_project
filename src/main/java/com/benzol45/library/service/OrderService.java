package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contains methods for processing orders (create/edit/delete/get/find)
 */

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<Order> getAll() {
        return orderRepository.findAll(Sort.by("createDate"));
    }

    public List<Order> getAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId, Sort.by("createDate"));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<Order> getAllByBook(Book book) {
        return orderRepository.findAllByBook(book, Sort.by("createDate"));
    }

    public boolean isOwner(Long orderId, User userDetails) {
        return orderRepository.findById(orderId).get().getUser().equals(userDetails);
    }

    @PreAuthorize("hasRole('READER')")
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @PreAuthorize("hasAnyRole('READER','LIBRARIAN')")
    public Order orderBook(Long bookId, Long userId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            log.debug("Can't find book by id " + bookId);
            return null;
        }
        Book book = optionalBook.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.debug("Can't find reader by id " + userId);
            return null;
        }
        User user = optionalUser.get();

        if (orderRepository.findByBookAndUser(book,user).isPresent()) {
            log.debug("Reader with id " + userId + " already has order for book with id " + bookId);
            return null;
        }

        Order newOrder = Order.builder()
                                .book(book)
                                .user(user)
                                .createDate(LocalDateTime.now())
                                .build();

        orderRepository.save(newOrder);
        return newOrder;
    }
}
