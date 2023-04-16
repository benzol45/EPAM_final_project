package com.benzol45.library.service;

import com.benzol45.library.entity.GivenBook;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {
    FineService fineService = new FineService();

    @Test
    void haveFain() {
        GivenBook givenBookPlus5Seconds = GivenBook.builder().returnDate(LocalDateTime.now().plusSeconds(5)).build();
        assertFalse(fineService.haveFain(givenBookPlus5Seconds));
        GivenBook givenBookNow = GivenBook.builder().returnDate(LocalDateTime.now()).build();
        assertFalse(fineService.haveFain(givenBookNow));
        GivenBook givenBookMinus1Minutes = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(1)).build();
        assertTrue(fineService.haveFain(givenBookMinus1Minutes));
        GivenBook givenBookMinus5Years = GivenBook.builder().returnDate(LocalDateTime.now().minusYears(5)).build();
        assertTrue(fineService.haveFain(givenBookMinus5Years));
    }

    @Test
    void calculateFineForGivenBook() {
        GivenBook givenBookPlus5Seconds = GivenBook.builder().returnDate(LocalDateTime.now().plusSeconds(5)).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookPlus5Seconds));
        GivenBook givenBookNow = GivenBook.builder().returnDate(LocalDateTime.now()).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookNow));

        //TODO внедрить хранитель свойств и брать результаты оттуда а не хардкодить
        GivenBook givenBookMinus1SecondsToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusSeconds(1)).inReadingRoom(true).build();
        assertEquals(1,fineService.calculateFineForGivenBook(givenBookMinus1SecondsToReadingRoom));
        GivenBook givenBookMinus1MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(1)).inReadingRoom(true).build();
        assertEquals(1,fineService.calculateFineForGivenBook(givenBookMinus1MinutesToReadingRoom));
        GivenBook givenBookMinus60MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(60)).inReadingRoom(true).build();
        assertEquals(1,fineService.calculateFineForGivenBook(givenBookMinus60MinutesToReadingRoom));
        GivenBook givenBookMinus61MinutesToReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(61)).inReadingRoom(true).build();
        assertEquals(2*1,fineService.calculateFineForGivenBook(givenBookMinus61MinutesToReadingRoom));

        GivenBook givenBookMinus1SecondsNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusSeconds(1)).inReadingRoom(false).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookMinus1SecondsNotReadingRoom));
        GivenBook givenBookMinus60MinutesNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().minusMinutes(60)).inReadingRoom(false).build();
        assertEquals(0,fineService.calculateFineForGivenBook(givenBookMinus60MinutesNotReadingRoom));
        GivenBook givenBookLastDayMinus2MinutesNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusMinutes(2)).inReadingRoom(false).build();
        assertEquals(10,fineService.calculateFineForGivenBook(givenBookLastDayMinus2MinutesNotReadingRoom));
        GivenBook givenBookLastDayMinus2HoursNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusHours(2)).inReadingRoom(false).build();
        assertEquals(10,fineService.calculateFineForGivenBook(givenBookLastDayMinus2HoursNotReadingRoom));
        GivenBook givenBookLastDayMinus26HoursNotReadingRoom = GivenBook.builder().returnDate(LocalDateTime.now().withHour(0).withMinute(0).minusHours(26)).inReadingRoom(false).build();
        assertEquals(2*10,fineService.calculateFineForGivenBook(givenBookLastDayMinus26HoursNotReadingRoom));
    }
}