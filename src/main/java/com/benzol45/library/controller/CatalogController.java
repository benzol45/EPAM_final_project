package com.benzol45.library.controller;

import com.benzol45.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
    private final BookService bookService;

    @Autowired
    public CatalogController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String getCatalogPage(Model model, @ModelAttribute PageableParam pageableParam, @RequestParam(value = "filter",required = false, defaultValue = "") String filter) {
        model.addAttribute("books", bookService.getListWithPaging(filter,pageableParam.getPageable()));
        model.addAttribute("page", pageableParam.getPage());
        model.addAttribute("totalPages", bookService.getTotalPages(filter,pageableParam.getPageable()));
        model.addAttribute("sort", pageableParam.getSort());
        model.addAttribute("filter", filter);
        return "BookCatalog";
        //TODO подобрать ширину столбцов в процентах от экрана
    }


}
