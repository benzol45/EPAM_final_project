package com.benzol45.library.controller;

import com.benzol45.library.service.BookService;
import com.benzol45.library.service.OrderService;
import com.benzol45.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class BookOperationController {
    private final BookService bookService;
    private final UserService userService;
    private final OrderService orderService;


    @Autowired
    public BookOperationController(BookService bookService, OrderService orderService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/book_order/{id}")
    //TODO hasRole("reader")
    //TODO сюда добавить в параметры пользователя и заменить хардкод на него
    public String orderBook(@PathVariable("id") Long id) {
        Long userId = 1L;

        orderService.orderBook(id, userId);

        //TODO перенаправлять в личный кабинет
        return "redirect:/";
    }

    @GetMapping("/book_give/{id}")
    //TODO hasRole("librarian")
    public String getGiveBookPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book",bookService.getById(id));
        model.addAttribute("readers", userService.getReaders());
        model.addAttribute("selectedReaderId", null);

        return "BookGive";
    }

    @PostMapping("/book_give")
    //TODO hasRole("librarian")
    public String giveBook(@RequestParam("book_id") Long bookId,
                           @RequestParam("reader_id") Long readerId,
                           @RequestParam("to_reading_room") Boolean toReadingRoom,
                           @RequestParam("return_date") LocalDateTime returnDate) {


        //TODO проверить возможность выдачи и выдать.
        return null;
    }

}
