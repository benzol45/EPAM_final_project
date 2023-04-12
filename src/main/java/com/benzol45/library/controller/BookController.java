package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.validator.BookUniqValidator;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.OrderService;
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
    private final OrderService orderService;

    @Autowired
    public BookController(BookService bookService, BookUniqValidator bookUniqValidator, OrderService orderService) {
        this.bookService = bookService;
        this.bookUniqValidator = bookUniqValidator;
        this.orderService = orderService;
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

    @GetMapping("/{id}/order")
    //TODO hasRole("reader")
    //TODO сюда добавить в параметры пользователя и заменить хардкод на него
    public String orderBook(@PathVariable("id") Long id) {
        Long userId = 1L;

        orderService.orderBook(id, userId);

        //TODO перенаправлять в личный кабинет
        return "redirect:/";
    }

    @GetMapping("/{id}/delete")
    //TODO hasRole("Admin")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteById(id);

        return "redirect:/catalog";
    }
}
