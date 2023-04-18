package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.validator.BookUniqValidator;
import com.benzol45.library.service.BookService;
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
    public String getNewBookPage(@ModelAttribute Book book) {
        return "BookEdit";
    }

    @GetMapping("/{id}/edit")
    public String getEditBookPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));

        return "BookEdit";
    }

    @PostMapping
    public String saveBook(@ModelAttribute @Valid Book book, Errors errors) {
        bookUniqValidator.validate(book, errors);
        if (errors.hasErrors()) {
            return "BookEdit";
        }

        bookService.save(book);

        return "redirect:/catalog";
    }

    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteById(id);

        return "redirect:/catalog";
    }

    //TODO add GET /book/{id}/info for book information
}
