package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ISBNservice {
    private final RestTemplate restTemplate;

    @Autowired
    public ISBNservice(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public static boolean isCorrect(String isbn) {
        isbn = prepareISBN(isbn);
        if (isbn.length()!=10 && isbn.length()!=13) {
            return false;
        }

    //https://ru.wikipedia.org/wiki/%D0%9C%D0%B5%D0%B6%D0%B4%D1%83%D0%BD%D0%B0%D1%80%D0%BE%D0%B4%D0%BD%D1%8B%D0%B9_%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9_%D0%BA%D0%BD%D0%B8%D0%B6%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80
        //TODO реализовать проверку по формату 10 и 13 символов   "978"+10
        return true;
    }

    private static String prepareISBN(String ISBN) {
        return ISBN.replaceAll("\\D","");
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Optional<String> getCoverImageURL(String isbn) {
        ResponseEntity<Map> entity = getResponseEntity(isbn);
        if (entity==null) {
            return Optional.empty();
        }

        Map body = entity.getBody();
        if (((List)body.get("covers")).isEmpty()) {
            return Optional.empty();
        }
        Integer coverId = (Integer) ((List)body.get("covers")).get(0);
        String url = "https://covers.openlibrary.org/b/id/"+coverId+"-M.jpg";

        return Optional.of(url);
    }

    private ResponseEntity<Map> getResponseEntity(String isbn) {
        isbn = prepareISBN(isbn);
        if (!isCorrect(isbn)) {
            return null;
        }

        ResponseEntity<Map> entity = null;
        try {
            entity = restTemplate.getForEntity("https://openlibrary.org/isbn/"+isbn+".json", Map.class);
            log.info("Request book info from openlibrary.org, ISBN: {}. Response code: {}",isbn,entity.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            return null;
        }

        if (entity.getStatusCode()!= HttpStatusCode.valueOf(200)) {
            return null;
        } else {
            return entity;
        }
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Optional<Book> fillByISBN(String isbn) {
        ResponseEntity<Map> entity = getResponseEntity(isbn);
        if (entity==null) {
            return Optional.empty();
        }
        Map body = entity.getBody();
        Book.BookBuilder bookBuilder = Book.builder();
        bookBuilder.ISBN((String) ((List)body.get("isbn_13")).get(0));
        bookBuilder.author(getAuthors(body));
        bookBuilder.title((String) body.get("title"));
        bookBuilder.pages(body.get("number_of_pages")!= null ? (Integer)body.get("number_of_pages") : 0);
        bookBuilder.publisher((String) ((List)body.get("publishers")).get(0));
        bookBuilder.dateOfPublication(getDate((String)body.get("publish_date")));

        return Optional.of(bookBuilder.build());
    }

    private String getAuthors(Map body) {
        Object works = body.get("works");
        if (works==null || ((List)works).isEmpty()) {
            return "";
        }

        String worksKey = (String) ((Map)((List)works).get(0)).get("key");

        Map worksValues = restTemplate.getForObject("https://openlibrary.org"+worksKey+".json", Map.class);
        Object authors = worksValues.get("authors");
        if (authors==null || ((List)authors).isEmpty()) {
            return "";
        }

        List authorsList = (List)authors;

        return (String) authorsList.stream()
                .map(o->((Map)o).get("author"))
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
