package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.GivenBookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GivingService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final GivenBookRepository givenBookRepository;
    private final OrderRepository orderRepository;
    private final RatingService ratingService;

    @Autowired
    public GivingService(BookRepository bookRepository, UserRepository userRepository, GivenBookRepository givenBookRepository, OrderRepository orderRepository, RatingService ratingService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.givenBookRepository = givenBookRepository;
        this.orderRepository = orderRepository;
        this.ratingService = ratingService;
    }

    @PreAuthorize("isAuthenticated()")
    public List<GivenBook> getAllByUserId(Long userId) {
        return givenBookRepository.findAllByUserId(userId, Sort.by("returnDate"));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<GivenBook> getAllByBook(Book book) {
        return givenBookRepository.findAllByBook(book, Sort.by("returnDate"));
    }

    @PreAuthorize("isAuthenticated()")
    public GivenBook getById(Long givenBookId) {
        Optional<GivenBook> optionalGivenBook = givenBookRepository.findById(givenBookId);
        if (optionalGivenBook.isEmpty()) {
            log.debug("Can't find the given book by id " + givenBookId);
            throw new IllegalStateException("Can't find the given book by id " + givenBookId);
        }

        return optionalGivenBook.get();
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<GivenBook> getAll() {
        return givenBookRepository.findAll(Sort.by("returnDate"));
    }

    public boolean canGiveBookById(Long bookId) {
        if (bookId==null || bookRepository.findById(bookId).isEmpty()) {
            log.debug("Can't find a book by id " + bookId);
            return false;
        }

        Book book = bookRepository.findById(bookId).get();
        return (canGiveBook(book));
    }

    public boolean canGiveBook(Book book) {
        if (book==null) {
            log.debug("For checking 'canGive' send null as book reference");
            return false;
        }
        int copiesInLibrary = book.getQuantity();
        int copiesInUse = givenBookRepository.countAllByBook(book);
        int copiesFree = copiesInLibrary - copiesInUse;

        return (copiesFree>0);
    }

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public GivenBook giveBook(Long bookId, Long readerId, Long orderId, Boolean toReadingRoom, LocalDateTime returnDate) {
        if (!canGiveBookById(bookId)) {
            log.debug("Can't give the book with id " + bookId + ", don't have free copy");
            throw new IllegalStateException("Can't give the book, don't have free copy");
        }

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<User> optionalUser = userRepository.findById(readerId);
        if (optionalBook.isEmpty() || optionalUser.isEmpty()) {
            if (optionalUser.isEmpty()) {
                log.debug("Can't find reader by id " + readerId);
            }
            if (optionalBook.isEmpty()) {
                log.debug("Can't find book by id " + bookId);
            }
            throw new IllegalArgumentException("Can't find book or reader");
        }


        GivenBook givenBook = GivenBook.builder()
                                .book(optionalBook.get())
                                .user(optionalUser.get())
                                .inReadingRoom(toReadingRoom)
                                .givenDate(LocalDateTime.now())
                                .returnDate(returnDate)
                                .build();
        givenBook = givenBookRepository.save(givenBook);

        if (orderId!=null) {
            orderRepository.deleteById(orderId);
        }

        return givenBook;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public void returnBook(Long givenBookId) {
        createRatingRequest(givenBookId);

        givenBookRepository.deleteById(givenBookId);
    }

    private void createRatingRequest(Long givenBookId) {
        Optional<GivenBook> optionalGivenBook = givenBookRepository.findById(givenBookId);
        if (optionalGivenBook.isEmpty()) {
            log.debug("Can't find the given book by id " + givenBookId);
            throw new IllegalArgumentException("Incorrect given book index " + givenBookId);
        }
        GivenBook givenBook = optionalGivenBook.get();
        ratingService.createRatingRequest(givenBook.getUser(),givenBook.getBook());
    }

    public LocalDateTime getNextReturnDate(Book book) {
        return givenBookRepository.findAllByBook(book, Sort.by("returnDate")).stream()
                .map(GivenBook::getReturnDate)
                .findFirst().orElse(null);
    }
}
