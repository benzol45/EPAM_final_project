package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.User;
import com.benzol45.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository spyUserRepository;
    private Metrics spyMetrics;
    private User testUser;

    @BeforeEach
    public void prepareServiceInstance() {
        testUser = new User();
        testUser.setFullName("full name");
        testUser.setLogin("login");
        testUser.setPassword("password");

        spyUserRepository = spy(UserRepository.class);
        when(spyUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(spyUserRepository.findById(2L)).thenReturn(Optional.empty());
        when(spyUserRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userService = new UserService(spyUserRepository, NoOpPasswordEncoder.getInstance());

    }

    @Test
    void saveNewUser() {
        User user = userService.saveNewUser(testUser);

        verify(spyUserRepository,times(1)).save(testUser);
    }

    @Test
    void setUserRole() {
        User user = userService.setUserRole(2L, User.Role.READER);
        verify(spyUserRepository,times(0)).save(any());
        assertEquals(null, user);

        user = userService.setUserRole(1L, User.Role.READER);
        verify(spyUserRepository,times(1)).findById(1L);
        verify(spyUserRepository,times(1)).save(any());
        testUser.setRole(User.Role.READER);
        assertEquals(testUser, user);
    }

    @Test
    void unblock() {
        testUser.setBlocked(true);

        User user = userService.unblock(2L);
        verify(spyUserRepository,times(0)).save(any());
        assertEquals(null, user);

        user = userService.unblock(1L);
        verify(spyUserRepository,times(1)).save(any());
        testUser.setBlocked(false);
        assertEquals(testUser, user);
        assertFalse(user.isBlocked());
    }

    @Test
    void block() {
        testUser.setBlocked(false);

        User user = userService.block(2L);
        verify(spyUserRepository,times(0)).save(any());
        assertEquals(null, user);

        user = userService.block(1L);
        verify(spyUserRepository,times(1)).save(any());
        testUser.setBlocked(true);
        assertEquals(testUser, user);
        assertTrue(user.isBlocked());
    }

    @Test
    void getUser() {
        assertEquals(testUser,userService.getUser(testUser));
        assertThrows(IllegalStateException.class, ()->userService.getUser(new org.springframework.security.core.userdetails.User("null","null", List.of(new SimpleGrantedAuthority("null")))));
    }
}