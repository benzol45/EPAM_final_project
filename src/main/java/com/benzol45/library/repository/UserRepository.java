package com.benzol45.library.repository;

import com.benzol45.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByLoginIgnoreCase(String login);

    List<User> findAllByRoleOrderByLogin(User.Role role);
    List<User> findAllByIsBlockedIsTrueAndRoleIsNotOrderByLogin(User.Role role);
    List<User> findAllByIsBlockedIsFalseAndRoleIsNotOrderByRoleAscLoginAsc(User.Role role);
    List<User> findAllByIsBlockedIsFalseAndRoleIsOrderByFullNameAsc(User.Role role);
}
