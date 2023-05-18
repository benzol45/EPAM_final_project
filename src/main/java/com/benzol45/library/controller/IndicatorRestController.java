package com.benzol45.library.controller;

import com.benzol45.library.service.IndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Get library indicators", description = "Present base indicators about state and working process this library")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of demand",content = @Content(examples = {
            @ExampleObject(name = "getLibraryIndicators",
                    summary = "Retrieves library Indicators.",
                    description = "Retrieves library Indicators.",
                    value = "{\"givenBook\": 50, \"orders\": 30, \"book_copies\": 500, \"readers\": 100}")}))
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
