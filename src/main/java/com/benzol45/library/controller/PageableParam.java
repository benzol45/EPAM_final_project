package com.benzol45.library.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
//https://www.baeldung.com/spring-data-jpa-pagination-sorting
public class PageableParam {
    private int page;
    private int bookOnPage;
    private String sort;

    public Pageable getPageable() {
        if (sort ==null || sort.isBlank()) {
            sort ="title";
        }

        if (page<0) {
            page=0;
        }

        if (!correctSorting(this.sort)) {
            log.debug("Incorrect sorting " + sort);
            //throw new IncorrectDataFromClientException("Incorrect sorting " + sort);
            sort ="title";
        }

        Sort sortResult = Sort.by(sort);
        if ("rating".equals(sort)) {
            sortResult = sortResult.descending();
        }

        return PageRequest.of(page,bookOnPage, sortResult);
    }

    private boolean correctSorting(String sorting) {
        List<String> correct = List.of("title","author","publisher","dateOfPublication","rating");
        return correct.contains(sorting);
    }
}
