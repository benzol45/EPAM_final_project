package com.benzol45.library.controller;

import com.benzol45.library.configuration.I18nUtil;
import com.benzol45.library.entity.Book;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.ISBNservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class IsbnController {
    private final ISBNservice isbNservice;
    private final BookService bookService;
    private final I18nUtil i18nUtil;

    @Autowired
    public IsbnController(ISBNservice isbNservice, BookService bookService, I18nUtil i18nUtil) {
        this.isbNservice = isbNservice;
        this.bookService = bookService;
        this.i18nUtil = i18nUtil;
    }

    @GetMapping("/isbn")
    public String getISBNPage() {
        return "ISBN";
    }

    @PostMapping("/isbn")
    public String processISBN(@ModelAttribute("isbn") String isbn, Model model) {

        if (!ISBNservice.isCorrect(isbn)) {
            model.addAttribute("error", i18nUtil.getMessage("isbn","incorrectError", LocaleContextHolder.getLocale()));
            return "ISBN";
        }
        if (bookService.getByISBN(isbn).isPresent()) {
            model.addAttribute("error", i18nUtil.getMessage("isbn","alreadyExistError", LocaleContextHolder.getLocale()));
            return "ISBN";
        }

        Optional<Book> bookOptional = isbNservice.fillByISBN(isbn);
        if (bookOptional.isEmpty()){
            model.addAttribute("error", i18nUtil.getMessage("isbn","notFindInService", LocaleContextHolder.getLocale()));
            return "ISBN";
        }

        model.addAttribute("book", bookOptional.get());
        isbNservice.getCoverImageURL(isbn).ifPresent(s -> model.addAttribute("externalCoverUrl", s));

        return "BookEdit";

    }
}
