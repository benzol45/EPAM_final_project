package com.benzol45.library.configuration.actuator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ISBNServiceHealthTest {

    @Test
    void health() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.getForEntity(contains("https://openlibrary.org/isbn"),any())).thenReturn(ResponseEntity.ok().body(new Object()));
        ISBNServiceHealth isbnServiceHealth = new ISBNServiceHealth(mockRestTemplate);
        assertEquals(Health.up().withDetail("ISBN", "9788498921939").withDetail("response", "OK").build(),isbnServiceHealth.health());

        mockRestTemplate = Mockito.mock(RestTemplate.class);
        when(mockRestTemplate.getForEntity(contains("https://openlibrary.org/isbn"),any())).thenThrow(HttpClientErrorException.NotFound.class);
        isbnServiceHealth = new ISBNServiceHealth(mockRestTemplate);
        assertEquals(Health.up().withDetail("ISBN", "9788498921939").withDetail("response", "Not found this code in database").build(),isbnServiceHealth.health());

        mockRestTemplate = Mockito.mock(RestTemplate.class);
        when(mockRestTemplate.getForEntity(contains("https://openlibrary.org/isbn"),any())).thenThrow(HttpClientErrorException.BadRequest.class);
        isbnServiceHealth = new ISBNServiceHealth(mockRestTemplate);
        assertEquals(Health.down().withDetail("ISBN", "9788498921939").withDetail("response", "Throw exception").build(),isbnServiceHealth.health());
    }
}