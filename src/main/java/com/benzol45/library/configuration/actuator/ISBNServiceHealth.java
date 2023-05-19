package com.benzol45.library.configuration.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ISBNServiceHealth implements HealthIndicator {
    private final RestTemplate restTemplate;
    private Health current = null;
    private LocalDateTime checkingTime = null;

    public ISBNServiceHealth(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        log.info("get ISBN state");
        if (current==null || LocalDateTime.now().isAfter(checkingTime.plusMinutes(1))) {
            checkStateISBNService();
        }

        if (current!=null) {
            return current;
        } else {
            return Health.unknown().build();
        }
    }

    private void checkStateISBNService() {
        log.info("recheck ISBN state");

        checkingTime = LocalDateTime.now();
        current = Health.unknown().build();

        String testISBN = "9788498921939";
        try {
            restTemplate.getForEntity("https://openlibrary.org/isbn/" + testISBN + ".json", String.class);
            current = Health.up().withDetail("ISBN", testISBN).withDetail("response", "OK").build();
        } catch (HttpClientErrorException.NotFound e) {
            current = Health.up().withDetail("ISBN", testISBN).withDetail("response", "Not found this code in database").build();
        } catch (Throwable e) {
            current = Health.down().withDetail("ISBN",testISBN).withDetail("response", "Throw exception").build();
        }
    }
}
