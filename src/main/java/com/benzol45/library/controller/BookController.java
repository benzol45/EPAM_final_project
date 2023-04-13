package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.validator.BookUniqValidator;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.OrderService;
import com.benzol45.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;
    private final BookUniqValidator bookUniqValidator;

    @Autowired
    public BookController(BookService bookService, BookUniqValidator bookUniqValidator) {
        this.bookService = bookService;
        this.bookUniqValidator = bookUniqValidator;
    }

    @GetMapping("/new")
    //TODO hasRole("Admin")
    public String getNewBookPage(@ModelAttribute Book book) {
        return "BookEdit";
    }

    @GetMapping("/{id}/edit")
    //TODO hasRole("Admin")
    public String getEditBookPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));

        return "BookEdit";
    }

    @PostMapping
    //TODO hasRole("Admin")
    public String saveBook(@ModelAttribute @Valid Book book, Errors errors) {
        bookUniqValidator.validate(book, errors);
        if (errors.hasErrors()) {
            return "BookEdit";
        }

        bookService.save(book);

        return "redirect:/catalog";
    }

    @GetMapping("/{id}/delete")
    //TODO hasRole("Admin")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteById(id);

        return "redirect:/catalog";
    }
}
