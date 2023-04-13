package com.benzol45.library.service;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.UserRepository;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public List<User> getListWithoutRole() {
        return userRepository.findAllByRoleOrderByLogin(User.Role.NA);
    }

    public List<User> getListBlocked() {
        return userRepository.findAllByIsBlockedIsTrueAndRoleIsNotOrderByLogin(User.Role.NA);
    }

    public List<User> getListWorking() {
        return userRepository.findAllByIsBlockedIsFalseAndRoleIsNotOrderByRoleAscLoginAsc(User.Role.NA);
    }

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

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

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

    public List<User> getReaders() {
        return userRepository.findAllByIsBlockedIsFalseAndRoleIsOrderByFullNameAsc(User.Role.READER);
    }
}
