package com.benzol45.library.security;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionExpireListener implements ApplicationListener<SessionDestroyedEvent> {
    private final Metrics metrics;
    private final UserService userService;

    @Autowired
    public SessionExpireListener(Metrics metrics, UserService userService) {
        this.metrics = metrics;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        //if user login two times (login when already was login) security context isn't in event and can't get user information
        if (event.getSource() instanceof StandardSessionFacade standardSessionFacade) {
            SecurityContext securityContext = (SecurityContext)standardSessionFacade.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext==null) {
                log.warn("Incorrect session expired. Event source: " + event.getSource());
                throw new IllegalStateException("Incorrect session parameters");
            }
            User.Role role = userService.getRole((UserDetails)securityContext.getAuthentication().getPrincipal());
            metrics.loggedUserDecrease(role);
        } else {
            log.warn("Incorrect user session expired. Event source: " + event.getSource());
            throw new IllegalStateException("Incorrect user parameters");
        }
    }
}
