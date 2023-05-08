package com.benzol45.library.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * Catch exceptions with authentication errors
 */

@Component
@Slf4j
//https://stackoverflow.com/questions/44086409/capture-authentication-failure-in-spring-security
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String ip = "";
        try {
            ip = ((WebAuthenticationDetails)((UsernamePasswordAuthenticationToken)event.getSource()).getDetails()).getRemoteAddress();
        } catch (ClassCastException e) {
        }

        log.info("Authentication failed for user " + event.getAuthentication().getPrincipal() + " from address " + ip);
    }
}
