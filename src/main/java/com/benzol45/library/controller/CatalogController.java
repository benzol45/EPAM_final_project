package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.User;
import com.benzol45.library.property.PropertyHolder;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.GivingService;
import com.benzol45.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
    private final BookService bookService;
    private final GivingService givingService;
    private final UserService userService;
    private final PropertyHolder propertyHolder;

    @Autowired
    public CatalogController(BookService bookService, GivingService givingService, UserService userService, PropertyHolder propertyHolder) {
        this.bookService = bookService;
        this.givingService = givingService;
        this.userService = userService;
        this.propertyHolder = propertyHolder;
    }

    @GetMapping
    public String getCatalogPage(Model model,
                                 @ModelAttribute PageableParam pageableParam, @RequestParam(value = "filter",required = false, defaultValue = "") String filter,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        pageableParam.setBookOnPage(propertyHolder.getBooksOnPage());
        List<Book> bookList = bookService.getListWithPaging(filter,pageableParam.getPageable());
        model.addAttribute("books", bookList);
        model.addAttribute("canGiveBooks", bookList.stream().filter(givingService::canGiveBook).collect(Collectors.toList()));
        model.addAttribute("page", pageableParam.getPage());
        model.addAttribute("totalPages", bookService.getTotalPages(filter,pageableParam.getPageable()));
        model.addAttribute("sort", pageableParam.getSort());
        model.addAttribute("filter", filter);
        if (userDetails!=null) {
            model.addAttribute("role", userService.getRole(userDetails).toString());
        }
        return "BookCatalog";
    }


}
