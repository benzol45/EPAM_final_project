package com.benzol45.library.integration;

import com.benzol45.library.entity.User;
import com.benzol45.library.repository.BookRepository;
import com.benzol45.library.security.UserDetailsServiceImpl;
import com.benzol45.library.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.security.Principal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters=false)
@TestPropertySource("/application_test.properties")
@Sql(value = "/clean_tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @WithUserDetails("admin")//  https://stackoverflow.com/questions/15203485/spring-test-security-how-to-mock-authentication
    void fullBookCRUDWayTest() throws Exception {
         //table with books is empty - doesn't have any table row <td
        mockMvc.perform(get("/catalog"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<td"))));
        assertEquals(0,bookRepository.findAll().size());

        //add 2 new books
        MockMultipartFile emptyFilePart = new MockMultipartFile("uploadCoverImage", "cover.jpg", "MediaType.IMAGE_JPEG", new byte[0]);
        mockMvc.perform(multipart("/book/save")
                        .file(emptyFilePart)
                        .param("ISBN","1234567890123")
                        .param("title","test title 1")
                        .param("author","author1, author2")
                        .param("pages","42")
                        .param("publisher","test publisher")
                        .param("dateOfPublication","2000-01-01")
                        .param("quantity","5")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog"));

        MockMultipartFile notEmptyFilePart = new MockMultipartFile("uploadCoverImage", "cover.jpg", "MediaType.IMAGE_JPEG", new byte[]{1,2,3,4,5});
        mockMvc.perform(multipart("/book/save")
                        .file(notEmptyFilePart)
                        .param("ISBN","1234567890124")
                        .param("title","test title 2")
                        .param("author","author3")
                        .param("pages","42")
                        .param("publisher","test publisher")
                        .param("dateOfPublication","2000-01-01")
                        .param("quantity","1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog"));


        //check that they are in catalog
        mockMvc.perform(get("/catalog"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test title 1")))
                .andExpect(content().string(containsString("test title 2")));
        assertEquals(2,bookRepository.findAll().size());

        //edit first
        MockMultipartFile emptyFileMPart = new MockMultipartFile("uploadCoverImage", "cover.jpg", "MediaType.IMAGE_JPEG", new byte[0]);
        mockMvc.perform(multipart("/book/save")
                        .file(notEmptyFilePart)
                        .param("id","1")
                        .param("ISBN","1234567890123")
                        .param("title","test title new version 1")
                        .param("author","author1")
                        .param("pages","142")
                        .param("publisher","new test publisher")
                        .param("dateOfPublication","2000-02-02")
                        .param("quantity","3")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog"));

        //check that it changed
        mockMvc.perform(get("/catalog"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test title new version 1")))
                .andExpect(content().string(containsString("test title 2")));

        //delete one
        mockMvc.perform(get("/book/1/delete"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog"));

        //check that have only one
        mockMvc.perform(get("/catalog"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("test title new version 1"))))
                .andExpect(content().string(containsString("test title 2")));
        assertEquals(1,bookRepository.findAll().size());

        //check that can get book info
        mockMvc.perform(get("/book/2/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test title 2")))
                .andExpect(content().string(containsString("data:image/jpg;base64,AQIDBAU=")));
    }

}
