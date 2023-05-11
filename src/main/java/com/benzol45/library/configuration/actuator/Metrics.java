package com.benzol45.library.configuration.actuator;

import com.benzol45.library.entity.User;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Metrics {
    private final MeterRegistry meterRegistry;

    @Autowired
    public Metrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void loggedUserIncrease(User.Role role) {
        meterRegistry.counter("library.logged_user","role", role.name().toLowerCase()).increment();
    }

    public void loggedUserDecrease(User.Role role) {
        meterRegistry.counter("library.logged_user","role", role.name().toLowerCase()).increment(-1);
    }
}
