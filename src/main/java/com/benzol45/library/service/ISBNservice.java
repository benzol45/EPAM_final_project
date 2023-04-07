package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

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
        //TODO реализовать проверку по формату 10 и 13 символов
        return true;
    }

    public Optional<Book> fillByISBN(String isbn) {
        if (!isCorrect(isbn)) {
            return Optional.empty();
        }

        ResponseEntity<Map> entity = restTemplate.getForEntity("https://openlibrary.org/isbn/"+isbn+".json", Map.class);
        log.debug("Request book info from openlibrary.org, ISBN: {}. Response code: {}",isbn,entity.getStatusCode().value());
        if (entity.getStatusCode()!= HttpStatusCode.valueOf(200)) {
            return Optional.empty();
        }
        Map body = entity.getBody();
        Book.BookBuilder bookBuilder = Book.builder();

        bookBuilder.ISBN((String) ((ArrayList)body.get("isbn_13")).get(0));
        //TODO реализовать запрос авторов
        //                .author("")   ((ArrayList)body.get("authors")) там мапа в которой ссылки на авторов restTemplate.getForEntity("https://openlibrary.org/authors/OL34184A.json", Map.class);
        bookBuilder.title((String) body.get("title"));
        bookBuilder.publisher((String) ((ArrayList)body.get("publishers")).get(0));
        //TODO реализовать парсинг даты
//                .dateOfPublication(body.get("publish_date"))   October 1, 1988

        return Optional.of(bookBuilder.build());
    }
}
