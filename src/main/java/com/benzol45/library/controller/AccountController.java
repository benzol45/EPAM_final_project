package com.benzol45.library.controller;

import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.Order;
import com.benzol45.library.entity.Rating;
import com.benzol45.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AccountController {
    private final OrderService orderService;
    private final GivingService givingService;
    private final FineService fineService;
    private final UserService userService;
    private final RatingService ratingService;

    @Autowired
    public AccountController(OrderService orderService, GivingService givingService, FineService fineService, UserService userService, RatingService ratingService) {
        this.orderService = orderService;
        this.givingService = givingService;
        this.fineService = fineService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @ModelAttribute("dateFormatter")
    public DateTimeFormatter addDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    }

    @GetMapping("/account/librarian")
    public String getLibrarianAccountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("librarian", userService.getUser(userDetails));

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

    @GetMapping("/reader/{id}")
    public String getReaderInfoPage(Model model, @PathVariable("id") Long readerId) {
        model.addAllAttributes(prepareReaderModel(readerId));

        return "AccountReader";
    }

    @GetMapping("/account/reader")
    public String getReaderAccountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long readerId = userService.getUser(userDetails).getId();

        model.addAllAttributes(prepareReaderModel(readerId));

        List<Rating> ratingRequests = ratingService.getAllRatingRequestByUser(userService.getUser(userDetails));
        if (!ratingRequests.isEmpty()) {
            model.addAttribute("rating_requests",ratingRequests);
        }

        return "AccountReader";
    }

    private Map<String,Object> prepareReaderModel(Long readerId) {
        Map<String,Object> model = new HashMap<>();

        model.put("reader", userService.getById(readerId));

        List<Order> orderList = orderService.getAllByUserId(readerId);
        model.put("orders", orderList);

        List<GivenBook> givenBooks = givingService.getAllByUserId(readerId);
        model.put("given_books", givenBooks);
        model.put("fines",givenBooks.stream()
                .collect(Collectors.toMap(
                        givenBook -> givenBook,
                        givenBook -> fineService.calculateFineForGivenBook(givenBook))));

        return model;
    }
}
