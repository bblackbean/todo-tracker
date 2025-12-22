package com.bblackbean.todo_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityOAuth2FlowTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void unauthenticated_request_to_protected_endpoint_redirects_to_login() throws Exception {
        mockMvc.perform(get("/api/weather").param("lat", "37.5665").param("lon", "126.9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void permitAll_login_page_is_accessible() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }
}
