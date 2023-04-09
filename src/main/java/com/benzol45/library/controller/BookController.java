package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/new")
    public String getNewBookPage(Model model) {
        model.addAttribute("book", new Book());

        return "BookEdit";
    }

    @GetMapping("/edit/{id}")
    public String getEditBookPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));

        return "BookEdit";
    }


    @PostMapping
    public String saveBook(@Valid Book book, Errors errors) {
        //TODO сохранение / переписываение книги + валидация
        if (errors.hasErrors()) {
            return "BookEdit";
        }

        return "redirect:/";
    }
}
