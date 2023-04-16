package com.benzol45.library.controller;

import com.benzol45.library.service.*;
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
    private final GivingService givingService;
    private final FineService fineService;


    @Autowired
    public BookOperationController(BookService bookService, OrderService orderService, UserService userService, GivingService givingService, FineService fineService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
        this.givingService = givingService;
        this.fineService = fineService;
    }

    @GetMapping("/book_order/{id}")
    //TODO hasRole("reader")
    //TODO сюда добавить в параметры пользователя и заменить хардкод на него
    public String orderBook(@PathVariable("id") Long id) {
        Long userId = 1L;

        orderService.orderBook(id, userId);

        //TODO перенаправлять в личный кабинет
        return "redirect:/catalog";
    }

    @GetMapping("/book_give/{id}")
    //TODO hasRole("librarian")
    public String getGiveBookPage(@PathVariable("id") Long id,
                                  @RequestParam(value = "readerId", required = false) Long readerId,
                                  @RequestParam(value = "orderId", required = false) Long orderId,
                                  Model model) {

        model.addAttribute("book",bookService.getById(id));
        model.addAttribute("readers", userService.getReaders());
        model.addAttribute("selectedReaderId", readerId);
        model.addAttribute("orderId", orderId);

        return "BookGive";
    }

    @PostMapping("/book_give")
    //TODO hasRole("librarian")
    public String giveBook(@RequestParam("book_id") Long bookId,
                           @RequestParam("reader_id") Long readerId,
                           @RequestParam(value = "order_id", required = false) Long orderId,
                           @RequestParam(value = "to_reading_room", defaultValue = "false") Boolean toReadingRoom,
                           @RequestParam("return_date") LocalDateTime returnDate) {

        if (givingService.canGiveBookById(bookId)) {
            givingService.giveBook(bookId, readerId, orderId, toReadingRoom, returnDate);
            if (orderId!=null) {
                return "redirect:/account/librarian";
            } else {
                return "redirect:/catalog";
            }
        } else {
            //TODO не можем выдать, ошибку показать
            return null;
        }
    }

    @GetMapping("book_return/{given_book_id}/")
    public String returnBook(@PathVariable("given_book_id") Long givenBookId) {
        //TODO hasRole("librarian")
        long fine = fineService.calculateFineForGivenBook(givingService.getById(givenBookId));

        if (fine>0) {
            //TODO отправить на страницу с информацией о штрафе и кнопкой "Штраф оплачен" и только ОТТУДА проводить возврат книги.
            return null;
        } else {
            givingService.returnBook(givenBookId);
            return "redirect:/account/librarian";
        }
    }

}
