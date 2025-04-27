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
class ParkingSpotControllerTest {

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(5)
    void testSpotStatus() throws Exception {
        mockMvc.perform(post("/spot-status")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(APPLICATION_JSON_UTF8)
                        .content("""
                            {
                                "lat": -24.5616840,
                                "lng": -46.6559810
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(true))
                .andExpect(jsonPath("$.license_plate").value("ZUL0001"))
                .andExpect(jsonPath("$.price_until_now").value(12.75));
    }

}
