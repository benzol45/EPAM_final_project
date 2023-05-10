package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Rating;
import com.benzol45.library.entity.User;
import com.benzol45.library.exception.IncorrectDataFromClientException;
import com.benzol45.library.exception.ObjectNotFoundException;
import com.benzol45.library.repository.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contains methods for work with book rating
 */

@Service
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BookService bookService;

    @Autowired
    public RatingService(RatingRepository ratingRepository, BookService bookService) {
        this.ratingRepository = ratingRepository;
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public void createRatingRequest(User user, Book book) {
        Rating rating = Rating.builder().user(user).book(book).createDate(LocalDateTime.now()).build();
        ratingRepository.save(rating);
    }

    @PreAuthorize("hasRole('READER')")
    public List<Rating> getAllRatingRequestByUser(User user) {
        return ratingRepository.findAllByUserAndRateIsNull(user, Sort.by("createDate").descending());
    }

    @Transactional
    @PreAuthorize("hasRole('READER')")
    public void setRating(User user, Long ratingRequestId, int rate) {
        if (rate<0 || rate>10) {
            log.debug("Incorrect rate value " + rate + " from user " + user.getLogin());
            throw new IncorrectDataFromClientException("Incorrect rate");
        }

        Rating ratingRequest = ratingRepository.findById(ratingRequestId)
                .orElseThrow(()->new ObjectNotFoundException("Incorrect rating request"));
        if (!ratingRequest.getUser().equals(user)) {
            log.warn("Rating request for another reader. From " + user.getLogin() + ", request for user" + ratingRequest.getUser().getLogin());
            throw new IncorrectDataFromClientException("Rating request for another reader");
        }
        if (ratingRequest.getRate()!=null) {
            log.debug("Rating request already processed, id " + ratingRequestId);
            throw new IncorrectDataFromClientException("Rating request already processed");
        }

        ratingRequest.setRate(rate);
        ratingRequest.setRateDate(LocalDateTime.now());
        ratingRepository.save(ratingRequest);

        Book book = ratingRequest.getBook();
        bookService.setRatingForBook(book, getAverageRatingByBook(book));
    }

    public double getAverageRatingByBook(Book book) {
        if (book==null) {
            log.debug("Request rating for null book");
            throw new ObjectNotFoundException("Incorrect book");
        }

        Double rating = ratingRepository.sumRateByBook(book).doubleValue() / ratingRepository.countRateByBook(book);
        return ((double)Math.round(rating*10))/10;
    }
}
