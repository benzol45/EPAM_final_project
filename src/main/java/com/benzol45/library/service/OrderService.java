package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
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

    public List<Order> getAll() {
        return orderRepository.findAll(Sort.by("createDate"));
    }

    public List<Order> getAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId, Sort.by("createDate"));
    }

    public boolean isOwner(Long orderId, User userDetails) {
        return orderRepository.findById(orderId).get().getUser().equals(userDetails);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public Order orderBook(Long bookId, Long userId) {
        //Книга есть и пользователь есть
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            //TODO log issue
            return null;
        }
        Book book = optionalBook.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            //TODO log issue
            return null;
        }
        User user = optionalUser.get();

        //У этого пользователя нет заказа на эту книгу
        if (orderRepository.findByBookAndUser(book,user).isPresent()) {
            //TODO log issue
            return null;
        }

        Order newOrder = Order.builder()
                                .book(book)
                                .user(user)
                                .createDate(LocalDateTime.now())
                                .build();

        return orderRepository.save(newOrder);
    }
}
