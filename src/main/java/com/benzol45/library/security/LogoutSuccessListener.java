package com.benzol45.library.security;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogoutSuccessListener implements ApplicationListener<LogoutSuccessEvent> {
    private final Metrics metrics;
    private final UserService userService;

    @Autowired
    public LogoutSuccessListener(Metrics metrics, UserService userService) {
        this.metrics = metrics;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            User.Role role = userService.getRole((UserDetails)usernamePasswordAuthenticationToken.getPrincipal());

            //Don't register here because already registered in SessionDestroyedEvent
            //metrics.loggedUserDecrease(role);
        } else {
            log.warn("Incorrect user logout registered. Event source: " + event.getSource());
            throw new IllegalStateException("Incorrect user parameters");
        }
    }
}
