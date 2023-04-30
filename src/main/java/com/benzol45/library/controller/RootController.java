package com.benzol45.library.controller;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.property.PropertyHolder;
import com.benzol45.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
    private final PropertyHolder propertyHolder;
    private final UserService userService;

    @Autowired
    public RootController(PropertyHolder propertyHolder, UserService userService) {
        this.propertyHolder = propertyHolder;
        this.userService = userService;
    }

    @GetMapping
    public String getRootPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails!=null) {
            model.addAttribute("role", userService.getRole(userDetails).toString());
        }
        model.addAttribute("hour_fine", propertyHolder.getFineForHourInReadingRoom());
        model.addAttribute("day_fine", propertyHolder.getFineForDayOnSubscription());
        return "Root";
    }
}
