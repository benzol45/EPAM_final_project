package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import com.benzol45.library.exception.IncorrectDataFromClientException;
import com.benzol45.library.exception.ObjectNotFoundException;
import com.benzol45.library.repository.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GivingServiceTest {

    @Test
    void getById() {
        GivenBook testGivenBook = new GivenBook();
        GivenBookRepository givenBookRepository = mock(GivenBookRepository.class);
        when(givenBookRepository.findById(1L)).thenReturn(Optional.of(testGivenBook));
        when(givenBookRepository.findById(2L)).thenReturn(Optional.empty());

        GivingService givingService = new GivingService(null,null,givenBookRepository,null,null, null);
        assertEquals(testGivenBook, givingService.getById(1L));
        assertThrows(ObjectNotFoundException.class, ()->givingService.getById(2L));
    }

    @Test
    void canGiveBookById() {
        Book testBook = Book.builder().id(1L).quantity(3).build();

        GivenBookRepository mockGivenBookRepository = mock(GivenBookRepository.class);
        BookRepository mockBookRepository = mock(BookRepository.class);
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(mockBookRepository.findById(2L)).thenReturn(Optional.empty());
        GivingService givingService = new GivingService(mockBookRepository,null,mockGivenBookRepository,null,null, null);

        assertFalse(()->givingService.canGiveBookById(2L));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(0);
        assertTrue(()->givingService.canGiveBookById(1L));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(2);
        assertTrue(()->givingService.canGiveBookById(1L));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(3);
        assertFalse(()->givingService.canGiveBookById(1L));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(4);
        assertFalse(()->givingService.canGiveBookById(1L));
    }

    @Test
    void canGiveBook() {
        Book testBook = Book.builder().id(1L).quantity(3).build();

        GivenBookRepository mockGivenBookRepository = mock(GivenBookRepository.class);
        GivingService givingService = new GivingService(null,null,mockGivenBookRepository,null,null, null);

        assertFalse(()->givingService.canGiveBook(null));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(0);
        assertTrue(()->givingService.canGiveBook(testBook));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(2);
        assertTrue(()->givingService.canGiveBook(testBook));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(3);
        assertFalse(()->givingService.canGiveBook(testBook));

        when(mockGivenBookRepository.countAllByBook(testBook)).thenReturn(4);
        assertFalse(()->givingService.canGiveBook(testBook));
    }

    @Test
    void giveBook() {
        Book testBook = Book.builder().id(1L).quantity(3).build();
        User testUser = new User();
        LocalDateTime testGivenLocalDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime testReturnLocalDateTime = LocalDateTime.now().plusDays(1);
        GivenBook testGivenBook = GivenBook.builder().book(testBook).user(testUser).givenDate(testGivenLocalDateTime).returnDate(testReturnLocalDateTime).inReadingRoom(false).build();

        BookRepository mockBookRepository = mock(BookRepository.class);
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        UserRepository mockUserRepository = mock(UserRepository.class);
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        GivenBookRepository givenBookRepository = mock(GivenBookRepository.class);
        when(givenBookRepository.countAllByBook(testBook)).thenReturn(2);
        when(givenBookRepository.save(any())).thenReturn(testGivenBook);

        OrderRepository spyOrderRepository = spy(OrderRepository.class);

        Metrics spyMetrics = spy(new Metrics(null,null,null,null,null));
        doNothing().when(spyMetrics).refreshGivenBooksCounter();
        doNothing().when(spyMetrics).refreshOrderCounter();

        GivingService givingService = new GivingService(mockBookRepository,mockUserRepository,givenBookRepository,spyOrderRepository,null, spyMetrics);
        GivenBook givenBook = givingService.giveBook(1L,1L, 1L ,false, testReturnLocalDateTime);

        verify(spyOrderRepository,times(1)).deleteById(1L);
        assertEquals(testBook,givenBook.getBook());
        assertEquals(testUser,givenBook.getUser());
        assertEquals(false,givenBook.isInReadingRoom());
        assertEquals(testGivenLocalDateTime,givenBook.getGivenDate());
        assertEquals(testReturnLocalDateTime,givenBook.getReturnDate());
        verify(spyMetrics,times(1)).refreshGivenBooksCounter();
        verify(spyMetrics,times(1)).refreshOrderCounter();

    }

    @Test
    void returnBook() {
        Book testBook = Book.builder().id(1L).quantity(3).build();
        User testUser = new User();
        LocalDateTime testGivenLocalDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime testReturnLocalDateTime = LocalDateTime.now().plusDays(1);
        GivenBook testGivenBook = GivenBook.builder().book(testBook).user(testUser).givenDate(testGivenLocalDateTime).returnDate(testReturnLocalDateTime).inReadingRoom(false).build();

        BookRepository mockBookRepository = mock(BookRepository.class);
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        UserRepository mockUserRepository = mock(UserRepository.class);
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        GivenBookRepository spyGivenBookRepository = spy(GivenBookRepository.class);
        when(spyGivenBookRepository.findById(1L)).thenReturn(Optional.of(testGivenBook));
        when(spyGivenBookRepository.findById(2L)).thenReturn(Optional.empty());

        OrderRepository spyOrderRepository = spy(OrderRepository.class);

        Metrics spyMetrics = spy(new Metrics(null,null,null,null,null));
        doNothing().when(spyMetrics).refreshGivenBooksCounter();

        RatingRepository mockRatingRepository = mock(RatingRepository.class);
        BookService spyBookService = spy(new BookService(mockBookRepository,spyMetrics));
        RatingService spyRatingService = spy(new RatingService(mockRatingRepository,spyBookService));

        GivingService givingService = new GivingService(mockBookRepository,mockUserRepository,spyGivenBookRepository,spyOrderRepository,spyRatingService, spyMetrics);

        assertThrows(ObjectNotFoundException.class,()->givingService.returnBook(2L));
        verify(spyMetrics,times(0)).refreshGivenBooksCounter();

        givingService.returnBook(1L);
        verify(spyGivenBookRepository,times(1)).deleteById(1L);
        verify(spyRatingService,times(1)).createRatingRequest(testUser,testBook);
        verify(spyMetrics,times(1)).refreshGivenBooksCounter();
    }

    @Test
    void returnDate() {
        GivingService givingService = new GivingService(null,null,null,null,null, null);

        assertThrows(IncorrectDataFromClientException.class, ()->givingService.checkReturnDate(true,LocalDateTime.now().minusDays(1)));

        givingService.checkReturnDate(true,LocalDateTime.now());
        givingService.checkReturnDate(true,LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        assertThrows(IncorrectDataFromClientException.class, ()->givingService.checkReturnDate(true,LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).plusSeconds(2)));
        assertThrows(IncorrectDataFromClientException.class, ()->givingService.checkReturnDate(true,LocalDateTime.now().plusYears(2)));

        givingService.checkReturnDate(false,LocalDateTime.now());
        givingService.checkReturnDate(false,LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        givingService.checkReturnDate(false,LocalDateTime.now().plusDays(25));
        assertThrows(IncorrectDataFromClientException.class, ()->givingService.checkReturnDate(false,LocalDateTime.now().plusDays(35)));
        assertThrows(IncorrectDataFromClientException.class, ()->givingService.checkReturnDate(false,LocalDateTime.now().plusYears(2)));


    }
}