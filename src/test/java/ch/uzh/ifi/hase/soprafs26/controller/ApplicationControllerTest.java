package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Application.class)
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void helloWorld_validInput_returnsStatusString() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/")
                .contentType(MediaType.TEXT_PLAIN);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().string("The application is running."));
    }
}