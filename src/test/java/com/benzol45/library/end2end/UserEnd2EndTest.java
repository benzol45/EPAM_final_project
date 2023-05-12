package com.benzol45.library.end2end;

import com.benzol45.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters=false)
@TestPropertySource("/application_test.properties")
@Sql(value = "/clean_tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserEnd2EndTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails("admin")
    void fullUserCRUDWayTest() throws Exception {

        //have only admin user
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[3]/div/tr/td[1]").string("admin"));
        assertEquals(1,userRepository.findAll().size());

        //add 2 new users
        mockMvc.perform(post("/user/new").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("fullName","reader")
                        .param("login","reader")
                        .param("password","reader"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/user/new").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("fullName","librarian")
                        .param("login","librarian")
                        .param("password","librarian"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        //check that they are without role
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[1]/div[1]/tr/td[1]").string("librarian"))
                .andExpect(xpath("/html/body/table[1]/div[2]/tr/td[1]").string("reader"))
                .andExpect(xpath("/html/body/table[3]/div/tr/td[1]").string("admin"));
        assertEquals(3,userRepository.findAll().size());

        //set role reader for first and librarian for second
        mockMvc.perform(get("/user/2/set/reader"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/admin"));
        mockMvc.perform(get("/user/3/set/librarian"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/admin"));

        //check that they have role
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(3))
                .andExpect(xpath("/html/body/table[3]/div[1]/tr/td[1]").string("admin"))
                .andExpect(xpath("/html/body/table[3]/div[2]/tr/td[1]").string("librarian"))
                .andExpect(xpath("/html/body/table[3]/div[3]/tr/td[1]").string("reader"));


        //block first
        mockMvc.perform(get("/user/2/block"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/admin"));

        //check that is blocked
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(1))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[2]/div/tr/td[1]").string("reader"))
                .andExpect(xpath("/html/body/table[3]/div[1]/tr/td[1]").string("admin"))
                .andExpect(xpath("/html/body/table[3]/div[2]/tr/td[1]").string("librarian"));

        //unblock first
        mockMvc.perform(get("/user/2/unblock"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/admin"));

        //check that isn't unblocked
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(3))
                .andExpect(xpath("/html/body/table[3]/div[1]/tr/td[1]").string("admin"))
                .andExpect(xpath("/html/body/table[3]/div[2]/tr/td[1]").string("librarian"))
                .andExpect(xpath("/html/body/table[3]/div[3]/tr/td[1]").string("reader"));

        //delete first
        mockMvc.perform(get("/user/2/delete"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/admin"));

        //check that have only second user
        mockMvc.perform(get("/user/admin"))
                //.andDo(print())
                .andExpect(xpath("/html/body/table[1]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[2]/div").nodeCount(0))
                .andExpect(xpath("/html/body/table[3]/div").nodeCount(2))
                .andExpect(xpath("/html/body/table[3]/div[1]/tr/td[1]").string("admin"))
                .andExpect(xpath("/html/body/table[3]/div[2]/tr/td[1]").string("librarian"));
        assertEquals(2,userRepository.findAll().size());
    }
}
