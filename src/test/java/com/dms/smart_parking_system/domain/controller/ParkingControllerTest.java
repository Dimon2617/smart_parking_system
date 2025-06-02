package com.dms.smart_parking_system.domain.controller;

import com.dms.smart_parking_system.domain.common.exception.CommonException;
import com.dms.smart_parking_system.domain.common.exception.ErrorCode;
import com.dms.smart_parking_system.domain.common.exception.handling.RestExceptionHandler;
import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.model.ParkingTicket;
import com.dms.smart_parking_system.domain.model.Vehicle;
import com.dms.smart_parking_system.domain.model.VehicleType;
import com.dms.smart_parking_system.domain.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParkingControllerTest {

    @Mock
    private ParkingService service;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ParkingController parkingController;

    private MockMvc mockMvc;
    private Vehicle vehicle;
    private VehicleDTO vehicleDTO;
    private ParkingSlot parkingSlot;
    private ParkingTicket parkingTicket;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        mockMvc = MockMvcBuilders.standaloneSetup(parkingController)
                .setControllerAdvice(new RestExceptionHandler(messageSource))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setLicensePlate("ABC123");
        vehicle.setType(VehicleType.CAR);

        parkingSlot = new ParkingSlot();
        parkingSlot.setId(1L);
        parkingSlot.setAvailable(true);

        parkingTicket = new ParkingTicket();
        parkingTicket.setId(1L);
        parkingTicket.setVehicle(vehicle);
        parkingTicket.setEntryTime(LocalDateTime.of(2025, 5, 31, 9, 0));
        parkingTicket.setSlot(parkingSlot);
        parkingTicket.setFee(BigDecimal.ZERO);

        vehicleDTO = new VehicleDTO();
        vehicleDTO.setLicensePlate("ABC123");
        vehicleDTO.setType(VehicleType.CAR);
    }

    @Test
    void checkIn_ShouldReturnCreatedTicket() throws Exception {
        when(service.checkIn(any(VehicleDTO.class))).thenReturn(parkingTicket);

        mockMvc.perform(post("/api/v1/parking/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"ABC123\",\"type\":\"CAR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehicle.licensePlate").value("ABC123"))
                .andExpect(jsonPath("$.vehicle.type").value("CAR"))
                .andExpect(jsonPath("$.entryTime").value("2025-05-31T09:00:00"))
                .andExpect(jsonPath("$.slot.id").value(1L))
                .andExpect(jsonPath("$.fee").value(0.0));

        verify(service).checkIn(any(VehicleDTO.class));
    }

    @Test
    void checkIn_AlreadyCheckedIn_ShouldReturnBadRequest() throws Exception {
        when(service.checkIn(any(VehicleDTO.class)))
                .thenThrow(new CommonException(ErrorCode.ALREADY_CHECKED_IN, HttpStatus.BAD_REQUEST,
                        List.of("Vehicle is already checked in.")));

        mockMvc.perform(post("/api/v1/parking/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"ABC123\",\"type\":\"CAR\"}"))
                .andExpect(status().isBadRequest());

        verify(service).checkIn(any(VehicleDTO.class));
    }

    @Test
    void checkIn_NoAvailableSlot_ShouldReturnNotFound() throws Exception {
        when(service.checkIn(any(VehicleDTO.class)))
                .thenThrow(new CommonException(ErrorCode.SLOT_NOT_AVAILABLE, HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/v1/parking/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"ABC123\",\"type\":\"CAR\"}"))
                .andExpect(status().isNotFound());

        verify(service).checkIn(any(VehicleDTO.class));
    }

    @Test
    void checkOut_ShouldReturnUpdatedTicket() throws Exception {
        parkingTicket.setExitTime(LocalDateTime.of(2025, 5, 31, 10, 0));
        parkingTicket.setFee(BigDecimal.valueOf(10.0));
        when(service.checkOut(anyString())).thenReturn(parkingTicket);

        mockMvc.perform(post("/api/v1/parking/checkout/ABC123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehicle.licensePlate").value("ABC123"))
                .andExpect(jsonPath("$.vehicle.type").value("CAR"))
                .andExpect(jsonPath("$.entryTime").value("2025-05-31T09:00:00"))
                .andExpect(jsonPath("$.exitTime").value("2025-05-31T10:00:00"))
                .andExpect(jsonPath("$.slot.id").value(1L))
                .andExpect(jsonPath("$.fee").value(10.0));

        verify(service).checkOut("ABC123");
    }

    @Test
    void checkOut_TicketNotFound_ShouldReturnNotFound() throws Exception {
        when(service.checkOut(anyString()))
                .thenThrow(new CommonException(ErrorCode.TICKET_NOT_FOUND, HttpStatus.NOT_FOUND,
                        List.of("Active ticket not found")));

        mockMvc.perform(post("/api/v1/parking/checkout/ABC123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).checkOut("ABC123");
    }

    @Test
    void getActiveTickets_ShouldReturnListOfTickets() throws Exception {
        when(service.getActiveTickets()).thenReturn(List.of(parkingTicket));

        mockMvc.perform(get("/api/v1/parking/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].vehicle.licensePlate").value("ABC123"))
                .andExpect(jsonPath("$[0].vehicle.type").value("CAR"))
                .andExpect(jsonPath("$[0].entryTime").value("2025-05-31T09:00:00"))
                .andExpect(jsonPath("$[0].slot.id").value(1L))
                .andExpect(jsonPath("$[0].fee").value(0.0));

        verify(service).getActiveTickets();
    }

    @Test
    void getActiveTickets_EmptyList_ShouldReturnEmptyList() throws Exception {
        when(service.getActiveTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/parking/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).getActiveTickets();
    }

}