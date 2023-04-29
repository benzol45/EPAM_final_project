package com.benzol45.library.entity.validator;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BookUniqValidator  implements Validator {
    private final BookRepository bookRepository;

    @Autowired
    public BookUniqValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz==Book.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        //If book is already saved in DB (has id) ISBN uniq validation doesn't make sense
        if (((Book)target).getId()!=null) {
            return;
        }

        if (!bookRepository.findByISBN(((Book)target).getISBN()).isEmpty()) {
            errors.rejectValue("ISBN","NotUniq","ISBN must be uniq");
        }
    }
}
