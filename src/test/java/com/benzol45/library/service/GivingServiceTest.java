package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
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

        GivingService givingService = new GivingService(null,null,givenBookRepository,null,null);
        assertEquals(testGivenBook, givingService.getById(1L));
        assertThrows(IllegalArgumentException.class, ()->givingService.getById(2L));
    }

    @Test
    void canGiveBookById() {
        Book testBook = Book.builder().id(1L).quantity(3).build();

        GivenBookRepository mockGivenBookRepository = mock(GivenBookRepository.class);
        BookRepository mockBookRepository = mock(BookRepository.class);
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(mockBookRepository.findById(2L)).thenReturn(Optional.empty());
        GivingService givingService = new GivingService(mockBookRepository,null,mockGivenBookRepository,null,null);

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
        GivingService givingService = new GivingService(null,null,mockGivenBookRepository,null,null);

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

        GivingService givingService = new GivingService(mockBookRepository,mockUserRepository,givenBookRepository,spyOrderRepository,null);
        GivenBook givenBook = givingService.giveBook(1L,1L, 1L ,false, testReturnLocalDateTime);

        verify(spyOrderRepository,times(1)).deleteById(1L);
        assertEquals(testBook,givenBook.getBook());
        assertEquals(testUser,givenBook.getUser());
        assertEquals(false,givenBook.isInReadingRoom());
        assertEquals(testGivenLocalDateTime,givenBook.getGivenDate());
        assertEquals(testReturnLocalDateTime,givenBook.getReturnDate());

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

        RatingRepository mockRatingRepository = mock(RatingRepository.class);
        BookService spyBookService = spy(new BookService(mockBookRepository,spyMetrics));
        RatingService spyRatingService = spy(new RatingService(mockRatingRepository,spyBookService));

        GivingService givingService = new GivingService(mockBookRepository,mockUserRepository,spyGivenBookRepository,spyOrderRepository,spyRatingService);

        assertThrows(IllegalArgumentException.class,()->givingService.returnBook(2L));

        givingService.returnBook(1L);
        verify(spyGivenBookRepository,times(1)).deleteById(1L);
        verify(spyRatingService,times(1)).createRatingRequest(testUser,testBook);
    }
}