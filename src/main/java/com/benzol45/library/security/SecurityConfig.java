package com.benzol45.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Handle SS exception and create custom accessDeniedPage https://www.baeldung.com/spring-security-custom-access-denied-page

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                    .requestMatchers("/", "/catalog", "/book/*/info", "/user/new", "/access_denied").permitAll()
                    .requestMatchers("/account/reader", "/book_order/*", "/order_cancel/*").hasRole("READER")
                    .requestMatchers("/account/librarian", "/reader/**", "/book_give/**", "/book_return/*", "/book_return_with_fine/*").hasRole("LIBRARIAN")
                    .requestMatchers("/user/**", "/book/**", "/isbn/**").hasRole("ADMINISTRATOR")
                    .anyRequest().denyAll().and()
                .exceptionHandling()
                    .accessDeniedPage("/access_denied").and()
                .formLogin()
                    .defaultSuccessUrl("/").and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID").and()
                .build();
    }
}
