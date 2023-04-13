package com.benzol45.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Locale;

@Data
@Entity
@Table(name = "users")
//TODO Реализовать UserDetails
//TODO нужно ли хранить язык для пользователя
//TODO валидация полей + уникальность на логин
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String fullName;

    private String contact;

    @Enumerated(EnumType.STRING) //default in SQL = EN
    private Language language;

    @NotNull
    @NotBlank
    private String login;

    @NotNull
    @NotBlank
    @Size(min = 6, message = "Password must be longer than 5 symbols")
    private String password;

    private boolean isBlocked;

    @Enumerated(EnumType.STRING)  //default in SQL = NA
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public enum Language {
        RU, EN
    }

    public enum Role {
        NA, READER, LIBRARIAN, ADMINISTRATOR
    }
}
