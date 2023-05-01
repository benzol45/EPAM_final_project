package com.benzol45.library.controller;

import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Locale;

@Controller
public class BookOperationController {
    private final BookService bookService;
    private final UserService userService;
    private final OrderService orderService;
    private final GivingService givingService;
    private final FineService fineService;
    private final RatingService ratingService;


    @Autowired
    public BookOperationController(BookService bookService, OrderService orderService, UserService userService, GivingService givingService, FineService fineService, RatingService ratingService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
        this.givingService = givingService;
        this.fineService = fineService;
        this.ratingService = ratingService;
    }

    @GetMapping("/book_order/{id}")
    public String orderBook(@PathVariable("id") Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        orderService.orderBook(bookId, userService.getUser(userDetails).getId());

        return "redirect:/catalog";
    }

    @PostMapping("/book_rate/{request_id}")
    public String rateBook(@PathVariable("request_id") Long requestId, @RequestParam("rate") Integer rate, @AuthenticationPrincipal UserDetails userDetails) {
        ratingService.setRating(userService.getUser(userDetails), requestId, rate);

        return "redirect:/account/reader";
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
        //TODO проверить дату выдачи, дату возврата

        if (givingService.canGiveBookById(bookId)) {
            givingService.giveBook(bookId, readerId, orderId, toReadingRoom, returnDate);
            if (orderId!=null) {
                return "redirect:/account/librarian";
            } else {
                return "redirect:/catalog";
            }
        } else {
            throw new IllegalStateException("Can't give book. Don't have free copies");
        }
    }

    @GetMapping("/book_return/{given_book_id}")
    public String returnBook(@PathVariable("given_book_id") Long givenBookId, Model model) {
        GivenBook givenBook = givingService.getById(givenBookId);
        long fine = fineService.calculateFineForGivenBook(givenBook);

        if (fine>0) {
            model.addAttribute("message", fineService.explainFine(givenBook, LocaleContextHolder.getLocale()));
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
        fineService.getFine(givingService.getById(givenBookId));
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
