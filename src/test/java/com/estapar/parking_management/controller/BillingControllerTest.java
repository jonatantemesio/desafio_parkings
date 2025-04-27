package com.estapar.parking_management.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 53264)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillingControllerTest {

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(7)
    void testRevenue() throws Exception {
        mockMvc.perform(post("/revenue")
                        .accept(APPLICATION_JSON_UTF8)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("""
                                {
                                  "date": "2025-01-02",
                                  "sector": "A"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(12.75))
                .andExpect(jsonPath("$.currency").value("BRL"));
    }

}


