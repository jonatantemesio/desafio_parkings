package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.SectorRepository;
import com.estapar.parking_management.repository.VehicleRepository;
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
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 53264)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GarageControllerTest {

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Test
    @Order(1)
    void createEntry() throws Exception {
        mockMvc.perform(post("/webhook")
                        .accept(APPLICATION_JSON_UTF8)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("""
                                {
                                  "license_plate": "ZUL0013",
                                  "entry_time": "2025-01-02T12:00:00.000Z",
                                  "event_type": "ENTRY"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void createParked() throws Exception {
        vehicleRepository.save(Vehicle.builder()
                        .licensePlate("TESTEPARK")
                        .sector(sectorRepository.findById(1L).get())
                        .entryTime(OffsetDateTime.parse("2025-01-02T12:00:00.000Z").toLocalDateTime())
                .build());

        mockMvc.perform(post("/webhook")
                        .accept(APPLICATION_JSON_UTF8)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("""
                                {
                                  "license_plate": "TESTEPARK",
                                  "lat": -23.5616840,
                                  "lng": -46.6559810,
                                  "event_type": "PARKED",
                                  "parked_time": "2025-01-02T12:05:00.000Z"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void createExist() throws Exception {
        vehicleRepository.save(Vehicle.builder()
                .licensePlate("TESTEPAR1")
                .sector(sectorRepository.findById(1L).get())
                .entryTime(OffsetDateTime.parse("2025-01-02T12:00:00.000Z").toLocalDateTime())
                .parkedTime(OffsetDateTime.parse("2025-01-02T12:05:00.000Z").toLocalDateTime())
                .build());

        mockMvc.perform(post("/webhook")
                        .accept(APPLICATION_JSON_UTF8)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("""
                                {
                                  "license_plate": "TESTEPAR1",
                                  "exit_time": "2025-01-02T13:00:00.000Z",
                                  "event_type": "EXIT"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void loadGarage() throws Exception {
        mockMvc.perform(get("/garage")
                        .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spots").isArray())
                .andExpect(jsonPath("$.garage").isArray())
                .andExpect(jsonPath("$.spots[0].id").value(1))
                .andExpect(jsonPath("$.garage[0].sector").value("A"));
    }
}
