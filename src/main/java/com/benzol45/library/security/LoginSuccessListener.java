package com.benzol45.library.security;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final Metrics metrics;
    private final UserService userService;

    @Autowired
    public LoginSuccessListener(Metrics metrics, UserService userService) {
        this.metrics = metrics;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String sessionId = null;
        Object details = event.getAuthentication().getDetails();
        if (details instanceof WebAuthenticationDetails webAuthenticationDetails) {
            sessionId = webAuthenticationDetails.getSessionId();
        }
        if (sessionId==null) {
            //Isn't work with session and storing it, only one request
            return;
        }

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            User.Role role = userService.getRole((UserDetails)usernamePasswordAuthenticationToken.getPrincipal());
            metrics.loggedUserIncrease(role);
        } else {
            log.warn("Incorrect user logged in into system. Event source: " + event.getSource());
            throw new IllegalStateException("Incorrect user parameters");
        }
    }
}
