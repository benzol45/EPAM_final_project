package com.benzol45.library.controller;

import com.benzol45.library.entity.Order;
import com.benzol45.library.service.GivingService;
import com.benzol45.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LibrarianAccountController {
    private final OrderService orderService;
    private final GivingService givingService;

    @Autowired
    public LibrarianAccountController(OrderService orderService, GivingService givingService) {
        this.orderService = orderService;
        this.givingService = givingService;
    }

    @GetMapping("/account/librarian")
    public String getLibrarianAccountPage(Model model) {
        List<Order> orderList = orderService.getAll();
        model.addAttribute("orders", orderList);
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
        model.addAttribute("canGiveOrders", orderList.stream().filter(order -> givingService.canGiveBook(order.getBook())).collect(Collectors.toList()));


        return "AccountLibrarian";
    }
}
