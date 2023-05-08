package com.benzol45.library.service;

import com.benzol45.library.configuration.I18nUtil;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.property.PropertyHolder;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Contains methods for working with the fines (creating/calculating/explaining)
 */

@Service
@Slf4j
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
        return ChronoUnit.DAYS.between(givenBook.getReturnDate().toLocalDate(), LocalDate.now());
    }

    @PreAuthorize("isAuthenticated()")
    public String explainFine(GivenBook givenBook, Locale locale) {
        if (!haveFine(givenBook)) {
            return i18nUtil.getMessage("fineService","noFine", locale);
        }

        StringBuilder stringBuilder = new StringBuilder();
        String timeUnit = givenBook.isInReadingRoom() ? i18nUtil.getMessage("fineService","hour", locale) : i18nUtil.getMessage("fineService","day", locale);
        String timeUnits = givenBook.isInReadingRoom() ? i18nUtil.getMessage("fineService","hours", locale) : i18nUtil.getMessage("fineService","days", locale);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        long fainPeriods = givenBook.isInReadingRoom() ? getHoursForFine(givenBook) : getDaysForFine(givenBook);
        long fainUnit = givenBook.isInReadingRoom() ? hourFain : dayFain;

        stringBuilder.append(i18nUtil.getMessage("fineService","line1.1", locale)).append(" ").append(givenBook.getUser().getFullName()).append(" ")
                .append(i18nUtil.getMessage("fineService","line1.2", locale)).append(" ").append(givenBook.getBook().getTitle()).append(" ")
                .append(givenBook.isInReadingRoom() ? i18nUtil.getMessage("fineService","readingRoom", locale) : i18nUtil.getMessage("fineService","subscription", locale)).append(" ")
                .append(i18nUtil.getMessage("fineService","line1.3", locale)).append(" ").append(fainPeriods).append(" ").append(timeUnits).append('\n');

        stringBuilder.append(i18nUtil.getMessage("fineService","line2.1", locale)).append(" ").append(givenBook.getReturnDate().format(dateTimeFormatter))
                .append(i18nUtil.getMessage("fineService","line2.2", locale)).append(" ").append(LocalDateTime.now().format(dateTimeFormatter)).append('\n');

        stringBuilder.append(i18nUtil.getMessage("fineService","line3.1", locale)).append(" ").append(timeUnit).append(" ")
                .append(i18nUtil.getMessage("fineService","line3.2", locale)).append(" ").append(fainUnit).append('\n');

        stringBuilder.append(i18nUtil.getMessage("fineService","line4.1", locale)).append(" ").append(fainPeriods).append(" * ").append(fainUnit)
                .append(" = ").append(calculateFineForGivenBook(givenBook));

        return stringBuilder.toString();
    }

    public void getFine(GivenBook givenBook) {
        log.info("Got the fine from " + givenBook.getUser().getFullName()
                + " for a book " + givenBook.getBook().getTitle()
                + " with id " + givenBook.getBook().getId()
                + ". Amount " + calculateFineForGivenBook(givenBook));
    }
}
