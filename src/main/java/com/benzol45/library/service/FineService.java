package com.benzol45.library.service;

import com.benzol45.library.configuration.I18nUtil;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.property.PropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
public class FineService {
    private final I18nUtil i18nUtil;
    private final PropertyHolder propertyHolder;
    private final int hourFain;
    private final int dayFain;


    @Autowired
    public FineService(I18nUtil i18nUtil, PropertyHolder propertyHolder) {
        this.i18nUtil = i18nUtil;
        this.propertyHolder = propertyHolder;
        this.hourFain = propertyHolder.getFineForHourInReadingRoom();
        this.dayFain = propertyHolder.getFineForDayOnSubscription();
    }

    @PreAuthorize("isAuthenticated()")
    public boolean haveFine(GivenBook givenBook) {
        return givenBook.getReturnDate().isBefore(LocalDateTime.now());
    }

    @PreAuthorize("isAuthenticated()")
    public long calculateFineForGivenBook(GivenBook givenBook) {
        if (!haveFine(givenBook)) {
            return 0;
        }

        if (givenBook.isInReadingRoom()) {
            long hoursForFain = getHoursForFine(givenBook);
            return hourFain * hoursForFain;
        } else {
            long daysForFain = getDaysForFine(givenBook);
            return dayFain * daysForFain;
        }
    }

    private long getHoursForFine(GivenBook givenBook) {
        long seconds = Duration.between(givenBook.getReturnDate(), LocalDateTime.now()).get(ChronoUnit.SECONDS);
        return seconds / (60*60) + ((seconds % (60*60) == 0) ? 0 : 1);
    }

    private long getDaysForFine(GivenBook givenBook) {
        return Period.between(givenBook.getReturnDate().toLocalDate(), LocalDate.now()).get(ChronoUnit.DAYS);
    }

    @PreAuthorize("isAuthenticated()")
    public String explainFine(GivenBook givenBook, Locale locale) {
        if (!haveFine(givenBook)) {
            return i18nUtil.getMessage("fineService","noFine", locale);
        }

        StringBuilder stringBuilder = new StringBuilder();
        String timeUnit = givenBook.isInReadingRoom() ? "hour" : "day";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        long fainPeriods = givenBook.isInReadingRoom() ? getHoursForFine(givenBook) : getDaysForFine(givenBook);
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
