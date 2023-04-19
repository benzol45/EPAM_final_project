package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.User;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.GivingService;
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

    @Autowired
    public CatalogController(BookService bookService, GivingService givingService) {
        this.bookService = bookService;
        this.givingService = givingService;
    }

    @GetMapping
    public String getCatalogPage(Model model,
                                 @ModelAttribute PageableParam pageableParam, @RequestParam(value = "filter",required = false, defaultValue = "") String filter,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        List<Book> bookList = bookService.getListWithPaging(filter,pageableParam.getPageable());
        model.addAttribute("books", bookList);
        model.addAttribute("canGiveBooks", bookList.stream().filter(givingService::canGiveBook).collect(Collectors.toList()));
        model.addAttribute("page", pageableParam.getPage());
        model.addAttribute("totalPages", bookService.getTotalPages(filter,pageableParam.getPageable()));
        model.addAttribute("sort", pageableParam.getSort());
        model.addAttribute("filter", filter);
        if (userDetails!=null) {
            if (!(userDetails instanceof User)) {
                //TODO ЭТОГО БЫТЬ НЕ ДОЛЖНО. Откуда то взялся пользователь реализованный не нашим пользователем
            }
            model.addAttribute("role", ((User)userDetails).getRole().toString());
        }
        return "BookCatalog";
        //TODO подобрать ширину столбцов в процентах от экрана
    }


}
