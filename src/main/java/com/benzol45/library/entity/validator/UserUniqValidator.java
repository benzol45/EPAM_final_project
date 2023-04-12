package com.benzol45.library.entity.validator;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserUniqValidator implements Validator {
    private final UserRepository userRepository;

    @Autowired
    public UserUniqValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == User.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (userRepository.findByLoginIgnoreCase(((User)target).getLogin()).isPresent()) {
            errors.rejectValue("login","","User with this login already exist");
        }
    }
}
