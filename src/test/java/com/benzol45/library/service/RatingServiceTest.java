package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Rating;
import com.benzol45.library.entity.User;
import com.benzol45.library.exception.IncorrectDataFromClientException;
import com.benzol45.library.exception.ObjectNotFoundException;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Test
    void setRating() {
        User user = new User(1L,"user","","user","",false, User.Role.READER);
        User finishedRatingUser = new User(2L,"user_","","user_","",false, User.Role.READER);
        Book book = Book.builder().id(1L).title("title").quantity(2).build();
        Rating rating = Rating.builder().book(book).user(user).build();
        Rating finishedRating = Rating.builder().book(book).user(finishedRatingUser).rate(5).build();

        BookRepository mockBookRepository = mock(BookRepository.class);
        Metrics spyMetrics = spy(new Metrics(null,null,null,null,null));
        BookService spyBookService = spy(new BookService(mockBookRepository,spyMetrics));

        RatingRepository spyRatingRepository = spy(RatingRepository.class);
        when(spyRatingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(spyRatingRepository.findById(2L)).thenReturn(Optional.of(finishedRating));
        when(spyRatingRepository.findById(3L)).thenReturn(Optional.empty());
        when(spyRatingRepository.sumRateByBook(book)).thenReturn(4);
        when(spyRatingRepository.countRateByBook(book)).thenReturn(2);

        RatingService ratingService = new RatingService(spyRatingRepository,spyBookService);
        assertThrows(IncorrectDataFromClientException.class,()->ratingService.setRating(user,2L,5));
        assertThrows(IncorrectDataFromClientException.class,()->ratingService.setRating(user,1L,11));
        assertThrows(IncorrectDataFromClientException.class,()->ratingService.setRating(user,1L,-1));

        assertThrows(IncorrectDataFromClientException.class,()->ratingService.setRating(finishedRatingUser,1L,5));
        assertThrows(IncorrectDataFromClientException.class,()->ratingService.setRating(finishedRatingUser,2L,5));
        assertThrows(ObjectNotFoundException.class,()->ratingService.setRating(user,3L,5));

        ratingService.setRating(user,1L,5);
        verify(spyBookService,times(1)).setRatingForBook(any(),eq(2.0));
        verify(spyRatingRepository,times(1)).save(any());
    }
}