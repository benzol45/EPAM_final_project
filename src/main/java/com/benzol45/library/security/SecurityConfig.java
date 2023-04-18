package com.benzol45.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                .requestMatchers("/catalog", "/book/*/info", "/user/new").permitAll()
                .requestMatchers("/account/reader", "/book_order/*", "/order_cancel/*").hasRole("READER")
                .requestMatchers("/account/librarian", "/book_give/**", "/book_return/*", "/book_return_with_fine/*").hasRole("LIBRARIAN")
                .requestMatchers("/user/**", "/book/**").hasRole("ADMINISTRATOR")
                //.requestMatchers("/**").permitAll()
                .and()
                .formLogin()
                    .defaultSuccessUrl("/catalog").and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/catalog")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID").and()
                .build();
    }
}
