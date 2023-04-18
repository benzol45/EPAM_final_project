package com.benzol45.library.service;

import com.benzol45.library.entity.GivenBook;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
            long hoursForFain = getHoursForFain(givenBook);
            return hourFain * hoursForFain;
        } else {
            long daysForFain = getDaysForFain(givenBook);
            return dayFain * daysForFain;
        }
    }

    private long getHoursForFain(GivenBook givenBook) {
        long seconds = Duration.between(givenBook.getReturnDate(), LocalDateTime.now()).get(ChronoUnit.SECONDS);
        return seconds / (60*60) + ((seconds % (60*60) == 0) ? 0 : 1);
    }

    private long getDaysForFain(GivenBook givenBook) {
        return Period.between(givenBook.getReturnDate().toLocalDate(), LocalDate.now()).get(ChronoUnit.DAYS);
    }

    public String explainFine(GivenBook givenBook) {
        if (!haveFain(givenBook)) {
            return "No fain";
        }

        StringBuilder stringBuilder = new StringBuilder();
        String timeUnit = givenBook.isInReadingRoom() ? "hour" : "day";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        long fainPeriods = givenBook.isInReadingRoom() ? getHoursForFain(givenBook) : getDaysForFain(givenBook);
        long fainUnit = givenBook.isInReadingRoom() ? hourFain : dayFain;

        stringBuilder.append("Reader ").append(givenBook.getUser().getFullName())
                .append(" delayed the book ").append(givenBook.getBook().getTitle())
                .append(givenBook.isInReadingRoom() ? " in the reading room" : " on a subscription")
                .append(" for ").append(fainPeriods).append(" ").append(timeUnit).append("s").append('\n');

        stringBuilder.append("Should have returned ").append(givenBook.getReturnDate().format(dateTimeFormatter))
                .append(", returning ").append(LocalDateTime.now().format(dateTimeFormatter)).append('\n');

        stringBuilder.append("The fine for every ").append(timeUnit)
                .append(" is ").append(fainUnit).append('\n');

        stringBuilder.append("Total fine is ").append(fainPeriods).append(" * ").append(fainUnit)
                .append(" = ").append(calculateFineForGivenBook(givenBook));

        return stringBuilder.toString();
    }


}
