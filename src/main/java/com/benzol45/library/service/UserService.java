package com.benzol45.library.service;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveNewUser(User user) {
        user.setLogin(user.getLogin().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLanguage(User.Language.EN);
        user.setBlocked(true);
        user.setRole(User.Role.NA);

        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<User> getReaders() {
        return userRepository.findAllByIsBlockedIsFalseAndRoleIsOrderByFullNameAsc(User.Role.READER);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<User> getListWithoutRole() {
        return userRepository.findAllByRoleOrderByLogin(User.Role.NA);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<User> getListBlocked() {
        return userRepository.findAllByIsBlockedIsTrueAndRoleIsNotOrderByLogin(User.Role.NA);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<User> getListWorking() {
        return userRepository.findAllByIsBlockedIsFalseAndRoleIsNotOrderByRoleAscLoginAsc(User.Role.NA);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public User setUserRole(Long id, User.Role role) {
        Optional<User> current = userRepository.findById(id);
        if (current.isPresent()) {
            User user = current.get();
            user.setRole(role);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public User unblock(Long id) {
        Optional<User> current = userRepository.findById(id);
        if (current.isPresent()) {
            User user = current.get();
            user.setBlocked(false);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public User block(Long id) {
        Optional<User> current = userRepository.findById(id);
        if (current.isPresent()) {
            User user = current.get();
            user.setBlocked(true);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    public User.Role getRole(UserDetails userDetails) {
        return getUser(userDetails).getRole();
    }

    public User getUser(UserDetails userDetails) {
        checkUserDetails(userDetails);
        return (User)userDetails;
    }

    private void checkUserDetails(UserDetails userDetails) {
        if (!(userDetails instanceof User)) {
            //TODO log issue
            throw new IllegalStateException("User and user parameters are incorrect");
        }
    }
}
