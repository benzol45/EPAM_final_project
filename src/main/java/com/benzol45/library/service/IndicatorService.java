package com.benzol45.library.service;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.GivenBookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class IndicatorService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final GivenBookRepository givenBookRepository;

    @Autowired
    public IndicatorService(BookRepository bookRepository, UserRepository userRepository, OrderRepository orderRepository, GivenBookRepository givenBookRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.givenBookRepository = givenBookRepository;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public Integer getBookCopyCounter() {
        Integer bookCopy = bookRepository.countBookCopy();
        return (bookCopy == null) ? 0 : bookCopy;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public Integer getReaderCounter() {
        return userRepository.countAllByRoleAndIsBlockedIsFalse(User.Role.READER);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public Integer getOrderCounter() {
        return Long.valueOf(orderRepository.count()).intValue();
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public Integer getGivenBooksCounter() {
        return Long.valueOf(givenBookRepository.count()).intValue();
    }
}
