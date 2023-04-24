package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.ISBNservice;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public IsbnController(ISBNservice isbNservice, BookService bookService) {
        this.isbNservice = isbNservice;
        this.bookService = bookService;
    }

    @GetMapping("/isbn")
    public String getISBNPage() {
        return "ISBN";
    }

    @PostMapping("/isbn")
    public String processISBN(@ModelAttribute("isbn") String isbn, Model model) {

        if (!ISBNservice.isCorrect(isbn)) {
            model.addAttribute("error", "Incorrect ISBN");
            return "ISBN";
        }
        if (bookService.getByISBN(isbn).isPresent()) {
            model.addAttribute("error", "Already in library's catalog");
            return "ISBN";
        }

        Optional<Book> bookOptional = isbNservice.fillByISBN(isbn);
        if (bookOptional.isEmpty()){
            model.addAttribute("error", "Can't find a book in the ISBN database");
            return "ISBN";
        }

        model.addAttribute("book", bookOptional.get());
        isbNservice.getCoverImageURL(isbn).ifPresent(s -> model.addAttribute("externalCoverUrl", s));

        return "BookEdit";

    }
}
