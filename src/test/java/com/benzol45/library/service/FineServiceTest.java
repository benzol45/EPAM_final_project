package com.benzol45.library.service;

import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.property.PropertyHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {
    static FineService fineService;
    static Long hourFine = 1L;
    static Long dayFine = 10L;

    @BeforeAll
    static void createFineService() {
        PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.setFineForHourInReadingRoom(hourFine.intValue());
        propertyHolder.setFineForDayOnSubscription(dayFine.intValue());
        fineService = new FineService(propertyHolder);
    }

    @Test
    void haveFain() {
        GivenBook givenBookPlus5Seconds = GivenBook.builder().returnDate(LocalDateTime.now().plusSeconds(5)).build();
        assertFalse(fineService.haveFine(givenBookPlus5Seconds));
        GivenBook givenBookNow = GivenBook.builder().returnDate(LocalDateTime.now()).build();
        assertFalse(fineService.haveFine(givenBookNow));
        GivenBook givenBookMinus1Minutes = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(1)).build();
        assertTrue(fineService.haveFine(givenBookMinus1Minutes));
        GivenBook givenBookMinus5Years = GivenBook.builder().returnDate(LocalDateTime.now().minusYears(5)).build();
        assertTrue(fineService.haveFine(givenBookMinus5Years));
    }

    @Test
    void calculateFineForGivenBook() {
        GivenBook givenBookPlus5Seconds = GivenBook.builder().returnDate(LocalDateTime.now().plusSeconds(5)).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookPlus5Seconds));
        GivenBook givenBookNow = GivenBook.builder().returnDate(LocalDateTime.now()).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookNow));

        GivenBook givenBookMinus1SecondsToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusSeconds(1)).inReadingRoom(true).build();
        assertEquals(hourFine,fineService.calculateFineForGivenBook(givenBookMinus1SecondsToReadingRoom));
        GivenBook givenBookMinus1MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(1)).inReadingRoom(true).build();
        assertEquals(hourFine,fineService.calculateFineForGivenBook(givenBookMinus1MinutesToReadingRoom));
        GivenBook givenBookMinus60MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(60)).inReadingRoom(true).build();
        assertEquals(hourFine,fineService.calculateFineForGivenBook(givenBookMinus60MinutesToReadingRoom));
        GivenBook givenBookMinus61MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(61)).inReadingRoom(true).build();
        assertEquals(2L*hourFine,fineService.calculateFineForGivenBook(givenBookMinus61MinutesToReadingRoom));

        GivenBook givenBookMinus1SecondsNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusSeconds(1)).inReadingRoom(false).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookMinus1SecondsNotReadingRoom));
        GivenBook givenBookMinus60MinutesNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(60)).inReadingRoom(false).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookMinus60MinutesNotReadingRoom));
        GivenBook givenBookLastDayMinus2MinutesNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusMinutes(2)).inReadingRoom(false).build();
        assertEquals(dayFine,fineService.calculateFineForGivenBook(givenBookLastDayMinus2MinutesNotReadingRoom));
        GivenBook givenBookLastDayMinus2HoursNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusHours(2)).inReadingRoom(false).build();
        assertEquals(dayFine,fineService.calculateFineForGivenBook(givenBookLastDayMinus2HoursNotReadingRoom));
        GivenBook givenBookLastDayMinus26HoursNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusHours(26)).inReadingRoom(false).build();
        assertEquals(2L*dayFine,fineService.calculateFineForGivenBook(givenBookLastDayMinus26HoursNotReadingRoom));
    }
}