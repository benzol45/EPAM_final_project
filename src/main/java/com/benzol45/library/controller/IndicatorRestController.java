package com.benzol45.library.controller;

import com.benzol45.library.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/indicators")
public class IndicatorRestController {
    private final IndicatorService indicatorService;

    @Autowired
    public IndicatorRestController(IndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Integer>> getLibraryIndicators() {
        return new ResponseEntity<>(
                Map.of(
                        "book_copies", indicatorService.getBookCopyCounter(),
                        "readers", indicatorService.getReaderCounter(),
                        "orders", indicatorService.getOrderCounter(),
                        "givenBook", indicatorService.getGivenBooksCounter()),
                HttpStatus.OK);
    }
}
