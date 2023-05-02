package com.benzol45.library.configuration.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ISBNServiceHealth implements HealthIndicator {
    private final RestTemplate restTemplate;

    public ISBNServiceHealth(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        String testISBN = "9788498921939";
        try {
            restTemplate.getForEntity("https://openlibrary.org/isbn/" + testISBN + ".json", String.class);
            return Health.up().withDetail("ISBN", testISBN).withDetail("response", "OK").build();
        } catch (HttpClientErrorException.NotFound e) {
            return Health.up().withDetail("ISBN", testISBN).withDetail("response", "Not found this code in database").build();
        } catch (Throwable e) {
            return Health.down().withDetail("ISBN",testISBN).withDetail("response", "Throw exception").build();
        }
    }
}
