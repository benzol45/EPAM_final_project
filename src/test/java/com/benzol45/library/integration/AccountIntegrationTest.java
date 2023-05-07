package com.benzol45.library.integration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters=false)
@TestPropertySource("/application_test.properties")
@Sql(value = {"/clean_tables.sql","/add_data_Account.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccountIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("reader")
    void readerAccountTest() throws Exception {
        //check that have two orders and two books
        mockMvc.perform(get("/account/reader"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]/a").string("title 1 (a)"))
                .andExpect(xpath("/html/body/table[1]/div[2]/tr/td[1]/a").string("title 2 (b)"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[1]/a").string("title 3 (c)"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[1]/a").string("title 4 (d)"));

        //cancel one order
        mockMvc.perform(get("/order_cancel/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/reader"));

        //check that have one order and two books
        mockMvc.perform(get("/account/reader"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]/a").string("title 2 (b)"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[1]/a").string("title 3 (c)"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[1]/a").string("title 4 (d)"));
    }

    @Test
    @WithUserDetails("librarian")
    void librarianAccountTest() throws Exception {
        //check that have two readers orders and two given books
        mockMvc.perform(get("/account/librarian"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]/a").string("title 1 (a)"))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[1]/div[2]/tr/td[1]/a").string("title 2 (b)"))
                .andExpect(xpath("/html/body/table[1]/div[2]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[1]/a").string("title 3 (c)"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[1]/a").string("title 4 (d)"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[2]/a").string("reader"));

        //give book from first order
        mockMvc.perform(get("/book_give/1?readerId=2&orderId=1").with(SecurityMockMvcRequestPostProcessors.csrf()))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("<form method=\"post\" action=\"/book_give\">")));

        mockMvc.perform(post("/book_give").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("book_id","1")
                        .param("reader_id","2")
                        .param("order_id","1")
                        .param("to_reading_room","false")
                        .param("return_date", LocalDate.now().toString()+"T23:59"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/librarian"));

        //check that have one reader order and three given books
        mockMvc.perform(get("/account/librarian"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(3))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]/a").string("title 2 (b)"))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[1]/a").string("title 3 (c)"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[1]/a").string("title 4 (d)"))
                .andExpect(xpath("/html/body/table[2]/div[2]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[3]/tr/td[1]/a").string("title 1 (a)"))
                .andExpect(xpath("/html/body/table[2]/div[3]/tr/td[2]/a").string("reader"));

        //return given book
        mockMvc.perform(get("/book_return/3"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/librarian"));

        //return given book with the fine
        mockMvc.perform(get("/book_return/1"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("<form action=\"/book_return_with_fine/1\" method=\"get\">")));

        mockMvc.perform(get("/book_return_with_fine/1"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/librarian"));

        //check that have one reader order and one given books
        mockMvc.perform(get("/account/librarian"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]/a").string("title 2 (b)"))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[2]/a").string("reader"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[1]/a").string("title 4 (d)"))
                .andExpect(xpath("/html/body/table[2]/div[1]/tr/td[2]/a").string("reader"));
    }
}
