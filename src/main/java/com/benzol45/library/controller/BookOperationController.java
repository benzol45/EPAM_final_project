package com.benzol45.library.controller;

import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String orderBook(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        orderService.orderBook(id, userService.getUser(userDetails).getId());

        return "redirect:/catalog";
    }

    @GetMapping("/book_give/{id}")
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

    @GetMapping("/book_return/{given_book_id}")
    public String returnBook(@PathVariable("given_book_id") Long givenBookId, Model model) {
        GivenBook givenBook = givingService.getById(givenBookId);
        long fine = fineService.calculateFineForGivenBook(givenBook);

        if (fine>0) {
            model.addAttribute("message", fineService.explainFine(givenBook));
            model.addAttribute("fain",fine);
            model.addAttribute("givenBook", givenBook);
            return "Fine";
        } else {
            givingService.returnBook(givenBookId);
            return "redirect:/account/librarian";
        }
    }

    @GetMapping("/book_return_with_fine/{given_book_id}")
    public String returnBookWithFine(@PathVariable("given_book_id") Long givenBookId) {
        //TODO залогировать что взяли штраф ? или записать в какую базу ?
        givingService.returnBook(givenBookId);
        return "redirect:/account/librarian";
    }

    @GetMapping("/order_cancel/{order_id}")
    public String cancelOrder(@PathVariable("order_id") Long orderId, @AuthenticationPrincipal UserDetails userDetails) {
        if (orderService.isOwner(orderId,(User)userDetails)) {
            orderService.deleteOrder(orderId);
        }

        return "redirect:/account/reader";
    }
}
