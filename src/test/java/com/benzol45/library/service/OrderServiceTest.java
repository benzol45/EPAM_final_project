package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.repository.OrderRepository;
import com.benzol45.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    BookRepository mockBookRepository;
    UserRepository mockUserRepository;
    OrderRepository spyOrderRepository;
    Metrics spyMetrics;
    OrderService orderService;

    @BeforeEach
    public void prepareServiceInstance() {
        mockBookRepository = mock(BookRepository.class);
        Book testBook = Book.builder().id(1L).title("title").quantity(1).build();
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(mockBookRepository.findById(2L)).thenReturn(Optional.empty());

        mockUserRepository = mock(UserRepository.class);
        User userHasOrder = new User(1L,"user1","","user1","",false, User.Role.READER);
        User userHasNotOrder = new User(2L,"user2","","user2","",false, User.Role.READER);
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userHasOrder));
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(userHasNotOrder));
        when(mockUserRepository.findById(3L)).thenReturn(Optional.empty());

        spyOrderRepository = spy(OrderRepository.class);
        Order testOrder = Order.builder().id(1L).book(testBook).user(userHasOrder).createDate(LocalDateTime.now()).build();
        when(spyOrderRepository.findByBookAndUser(testBook,userHasOrder)).thenReturn(Optional.of(testOrder));
        when(spyOrderRepository.findByBookAndUser(testBook,userHasNotOrder)).thenReturn(Optional.empty());

        orderService = new OrderService(spyOrderRepository,mockBookRepository,mockUserRepository);
    }

    @Test
    void orderBook() {
        Order returnedOrder;

        //Empty book
        returnedOrder = orderService.orderBook(2L,2L);
        assertNull(returnedOrder);
        verify(spyOrderRepository,times(0)).save(any());

        //Empty reader
        returnedOrder = orderService.orderBook(1L,3L);
        assertNull(returnedOrder);
        verify(spyOrderRepository,times(0)).save(any());

        //Reader already has order with this book
        returnedOrder = orderService.orderBook(1L,1L);
        assertNull(returnedOrder);
        verify(spyOrderRepository,times(0)).save(any());

        //all correct, order created and saved
        returnedOrder = orderService.orderBook(1L,2L);
        assertNotNull(returnedOrder);
        verify(spyOrderRepository,times(1)).save(any());
    }

    @Test
    void deleteOrder() {
        orderService.deleteOrder(1L);
        verify(spyOrderRepository,times(1)).deleteById(1L);
    }
}