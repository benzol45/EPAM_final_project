package com.benzol45.library.configuration.actuator;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.GivenBookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Metrics {
    private final MeterRegistry meterRegistry;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final GivenBookRepository givenBookRepository;
    private AtomicInteger bookCopiesGauges;
    private AtomicInteger readersGauges;
    private AtomicInteger ordersGauges;
    private AtomicInteger givenBooksGauges;

    @Autowired
    public Metrics(MeterRegistry meterRegistry, BookRepository bookRepository, UserRepository userRepository, OrderRepository orderRepository, GivenBookRepository givenBookRepository) {
        this.meterRegistry = meterRegistry;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.givenBookRepository = givenBookRepository;
    }

    public void initGauges() {
        bookCopiesGauges = meterRegistry.gauge("library.BookCopies", new AtomicInteger(0));
        readersGauges = meterRegistry.gauge("library.Readers", new AtomicInteger(0));
        ordersGauges = meterRegistry.gauge("library.Orders", new AtomicInteger(0));
        givenBooksGauges = meterRegistry.gauge("library.GivenBooks", new AtomicInteger(0));

    }

    public void refreshBookCopyCounter() {
        bookCopiesGauges.set(bookRepository.countBookCopy());
    }

    public void refreshReaderCounter() {
         readersGauges.set(userRepository.countAllByRoleAndIsBlockedIsFalse(User.Role.READER));
    }

    public void refreshOrderCounter() {
        ordersGauges.set(Long.valueOf(orderRepository.count()).intValue());
    }

    public void refreshGivenBooksCounter() {
        givenBooksGauges.set(Long.valueOf(givenBookRepository.count()).intValue());
    }
}
