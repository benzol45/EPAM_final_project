package com.benzol45.library.configuration;

import com.benzol45.library.configuration.actuator.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsInitializerOnStartUp {

    @Bean
    public ApplicationRunner applicationRunner(@Autowired Metrics metrics) {
        return (args) -> {
            metrics.initGauges();
            metrics.refreshBookCopyCounter();
            metrics.refreshReaderCounter();
            metrics.refreshOrderCounter();
            metrics.refreshGivenBooksCounter();
        };
    }
}
