package com.benzol45.library.controller;

import com.benzol45.library.entity.Book;
import com.benzol45.library.entity.GivenBook;
import com.benzol45.library.entity.User;
import com.benzol45.library.entity.validator.BookUniqValidator;
import com.benzol45.library.service.BookService;
import com.benzol45.library.service.GivingService;
import com.benzol45.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;
    private final BookUniqValidator bookUniqValidator;
    private final UserService userService;
    private final GivingService givingService;

    @Autowired
    public BookController(BookService bookService, BookUniqValidator bookUniqValidator, UserService userService, GivingService givingService) {
        this.bookService = bookService;
        this.bookUniqValidator = bookUniqValidator;
        this.userService = userService;
        this.givingService = givingService;
    }

    @GetMapping("/{id}/info")
    public String getBookInfoPage(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Book book =  bookService.getById(id);
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
        model.addAttribute("book", book);
        if (book.getImagePath()!=null && !book.getImagePath().isBlank()) {
            model.addAttribute("internalImageBase64", bookService.getBase64Cover(book));
        }

        model.addAttribute("have_free_copy", givingService.canGiveBook(book));
        model.addAttribute("date_free", givingService.getNextReturnDate(book));

        if (userDetails!=null && userService.getRole(userDetails)== User.Role.LIBRARIAN) {
            List<GivenBook> givenBooks = givingService.getAllByBook(book);
            if (!givenBooks.isEmpty()) {
                model.addAttribute("given_books", givenBooks);
            }
        }

        return "Book";
    }

    @GetMapping("/new")
    public String getNewBookPage(@ModelAttribute Book book) {
        return "BookEdit";
    }

    @GetMapping("/{id}/edit")
    public String getEditBookPage(@PathVariable("id") Long id, Model model) {
        Book book =  bookService.getById(id);
        model.addAttribute("book", book);
        if (book.getImagePath()!=null && !book.getImagePath().isBlank()) {
            model.addAttribute("internalImageBase64", bookService.getBase64Cover(book));
        }

        return "BookEdit";
    }

    @PostMapping("/new")
    public String saveBook(@ModelAttribute @Valid Book book, Errors errors,
                           @ModelAttribute("externalCoverUrl") String externalCoverUrl,
                           @RequestParam("uploadCoverImage") MultipartFile multipartFile) {
        bookUniqValidator.validate(book, errors);
        if (errors.hasErrors()) {
            return "BookEdit";
        }

        bookService.save(book);

        if (!multipartFile.isEmpty()) {
            bookService.setMultipartFileAsCoverImage(book, multipartFile);
        } else if (!externalCoverUrl.isBlank()) {
            bookService.setExternalFileAsCoverImage(book, externalCoverUrl);
        }

        return "redirect:/catalog";
    }

    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteById(id);

        return "redirect:/catalog";
    }

    //TODO add GET /book/{id}/info for book information
}
