package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.GivenBookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GivingService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final GivenBookRepository givenBookRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public GivingService(BookRepository bookRepository, UserRepository userRepository, GivenBookRepository givenBookRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.givenBookRepository = givenBookRepository;
        this.orderRepository = orderRepository;
    }

    public boolean canGiveBookById(Long bookId) {
        if (bookId==null || bookRepository.findById(bookId).isEmpty()) {
            //TODO log issue
            return false;
        }

        Book book = bookRepository.findById(bookId).get();
        return (canGiveBook(book));
    }

    public boolean canGiveBook(Book book) {
        if (book==null) {
            //TODO log issue
            return false;
        }
        int copiesInLibrary = book.getQuantity();
        int copiesInUse = givenBookRepository.countAllByBook(book);
        int copiesFree = copiesInLibrary - copiesInUse;

        return (copiesFree>0);
    }

    @Transactional
    public GivenBook giveBook(Long bookId, Long readerId, Long orderId, Boolean toReadingRoom, LocalDateTime returnDate) {
        if (!canGiveBookById(bookId)) {
            //TODO log issue
            //TODO поймать это исключение
            throw new IllegalStateException("Can't give the book, don't have free copy");
        }

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<User> optionalUser = userRepository.findById(readerId);
        if (optionalBook.isEmpty() || optionalUser.isEmpty()) {
            //TODO log issue
            //TODO поймать это исключение
            throw new IllegalArgumentException("Can't find book or reader");
        }


        GivenBook givenBook = GivenBook.builder()
                                .book(optionalBook.get())
                                .user(optionalUser.get())
                                .inReadingRoom(toReadingRoom)
                                .returnDate(returnDate)
                                .build();
        givenBook = givenBookRepository.save(givenBook);

        if (orderId!=null) {
            orderRepository.deleteById(orderId);
        }

        return givenBook;
    }
}
