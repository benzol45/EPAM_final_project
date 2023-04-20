package com.benzol45.library.controller;

import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.FineService;
import com.benzol45.library.service.GivingService;
import com.benzol45.library.service.OrderService;
import com.benzol45.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final OrderService orderService;
    private final GivingService givingService;
    private final FineService fineService;
    private final UserService userService;

    @Autowired
    public AccountController(OrderService orderService, GivingService givingService, FineService fineService, UserService userService) {
        this.orderService = orderService;
        this.givingService = givingService;
        this.fineService = fineService;
        this.userService = userService;
    }

    @ModelAttribute("dateFormatter")
    public DateTimeFormatter addDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    }

    @GetMapping("/librarian")
    public String getLibrarianAccountPage(Model model) {
        List<Order> orderList = orderService.getAll();
        model.addAttribute("orders", orderList);
        model.addAttribute("canGiveOrders", orderList.stream().filter(order -> givingService.canGiveBook(order.getBook())).collect(Collectors.toList()));

        List<GivenBook> givenBooks = givingService.getAll();
        model.addAttribute("given_books", givenBooks);
        model.addAttribute("fines",givenBooks.stream()
                .collect(Collectors.toMap(
                                givenBook -> givenBook,
                                givenBook -> fineService.calculateFineForGivenBook(givenBook))));

        return "AccountLibrarian";
    }

    @GetMapping("/reader")
    public String getReaderAccountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long readerId = userService.getUser(userDetails).getId();

        List<Order> orderList = orderService.getAllByUserId(readerId);
        model.addAttribute("orders", orderList);

        List<GivenBook> givenBooks = givingService.getAllByUserId(readerId);
        model.addAttribute("given_books", givenBooks);
        model.addAttribute("fines",givenBooks.stream()
                .collect(Collectors.toMap(
                        givenBook -> givenBook,
                        givenBook -> fineService.calculateFineForGivenBook(givenBook))));

        return "AccountReader";
    }
}
