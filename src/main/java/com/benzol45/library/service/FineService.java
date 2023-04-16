package com.benzol45.library.service;

import com.benzol45.library.entity.GivenBook;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
public class FineService {
    //TODO инжект проперти-холдера со ставками штрафов
    private final int hourFain = 1;
    private final int dayFain = 10;

    public boolean haveFain(GivenBook givenBook) {
        return givenBook.getReturnDate().isBefore(LocalDateTime.now());
    }

    public long calculateFineForGivenBook(GivenBook givenBook) {
        if (!haveFain(givenBook)) {
            return 0;
        }

        if (givenBook.isInReadingRoom()) {
            long minutes = Duration.between(givenBook.getReturnDate(), LocalDateTime.now()).get(ChronoUnit.SECONDS);
            long hoursForFain = minutes / (60*60) + ((minutes % (60*60) == 0) ? 0 : 1);
            return hourFain * hoursForFain;
        } else {
            long daysForFain = Period.between(givenBook.getReturnDate().toLocalDate(), LocalDate.now()).get(ChronoUnit.DAYS);
            return dayFain * daysForFain;
        }
    }
}
