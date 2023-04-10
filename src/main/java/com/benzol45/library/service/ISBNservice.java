package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

//TODO Попробовать отсюда доставать https://isbnsearch.org/isbn/9780544003415 картинки обложки

@Service
@Slf4j
public class ISBNservice {
    private final RestTemplate restTemplate;

    @Autowired
    public ISBNservice(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static boolean isCorrect(String isbn) {
    //https://ru.wikipedia.org/wiki/%D0%9C%D0%B5%D0%B6%D0%B4%D1%83%D0%BD%D0%B0%D1%80%D0%BE%D0%B4%D0%BD%D1%8B%D0%B9_%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9_%D0%BA%D0%BD%D0%B8%D0%B6%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80
        //TODO реализовать проверку по формату 10 и 13 символов   "978"+10
        return true;
    }

    public Optional<Book> fillByISBN(String isbn) {
        if (!isCorrect(isbn)) {
            return Optional.empty();
        }

        ResponseEntity<Map> entity = restTemplate.getForEntity("https://openlibrary.org/isbn/"+isbn+".json", Map.class);
        log.info("Request book info from openlibrary.org, ISBN: {}. Response code: {}",isbn,entity.getStatusCode().value());
        if (entity.getStatusCode()!= HttpStatusCode.valueOf(200)) {
            return Optional.empty();
        }
        Map body = entity.getBody();
        Book.BookBuilder bookBuilder = Book.builder();
        bookBuilder.ISBN((String) ((List)body.get("isbn_13")).get(0));
        bookBuilder.author(getAuthors((List)body.get("authors")));
        bookBuilder.title((String) body.get("title"));
        bookBuilder.pages((Integer)body.get("number_of_pages"));
        bookBuilder.publisher((String) ((List)body.get("publishers")).get(0));
        bookBuilder.dateOfPublication(getDate((String)body.get("publish_date")));

        return Optional.of(bookBuilder.build());
    }

    private String getAuthors(List<Map<String,String>> authors) {
        if (authors==null) {
            return "";
        }

        return authors.stream()
                .map(o->((Map)o).get("key"))
                .map(path->"https://openlibrary.org"+(String)path+".json")
                .map(link->restTemplate.getForObject((String) link, Map.class))
                .map(a->(String)((Map)a).get("name"))
                .collect(Collectors.joining(", "));
    }

    private LocalDate getDate(String dateString) {
        if (dateString.length()==4) { //Only year like 2022 or 2001
            return LocalDate.of(Integer.parseInt(dateString),12,31);
        }

        LocalDate result = null;
        try {
            result = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {}
        try {
            result = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {}
        try {
            result = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MM d, yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {}

        if (result==null) {
            log.warn("Can't parse date {}", dateString);
        }
        return result;
    }
}