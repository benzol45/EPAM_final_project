package com.benzol45.library.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Contains properties from environment
 */

@Component
@ConfigurationProperties(prefix = "library")
@Data
public class PropertyHolder {
    private int booksOnPage;
    private int fineForHourInReadingRoom;
    private int fineForDayOnSubscription;
}
