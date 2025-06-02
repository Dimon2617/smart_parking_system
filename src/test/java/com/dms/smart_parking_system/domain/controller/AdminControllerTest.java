package com.dms.smart_parking_system.domain.controller;

import com.dms.smart_parking_system.domain.dto.ParkingSlotDTO;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import com.dms.smart_parking_system.domain.service.ParkingLotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private ParkingLotService lotService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ParkingLot parkingLot;
    private ParkingSlotDTO parkingSlotDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Test Lot");

        parkingSlotDTO = new ParkingSlotDTO();
        parkingSlotDTO.setAvailable(true);
    }

    @Test
    void createLot_ShouldReturnCreatedLot() throws Exception {
        when(lotService.createLot(anyString())).thenReturn(parkingLot);

        mockMvc.perform(post("/api/v1/admin/lots")
                        .param("name", "Test Lot")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lot"));

        verify(lotService).createLot("Test Lot");
    }

    @Test
    void deleteLot_ShouldReturnNoContent() throws Exception {
        doNothing().when(lotService).deleteLot(anyLong());

        mockMvc.perform(delete("/api/v1/admin/lots/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(lotService).deleteLot(1L);
    }

    @Test
    void addLevel_ShouldReturnUpdatedLot() throws Exception {
        when(lotService.addLevel(anyLong(), anyInt())).thenReturn(parkingLot);

        mockMvc.perform(post("/api/v1/admin/lots/1/levels")
                        .param("number", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lot"));

        verify(lotService).addLevel(1L, 2);
    }

    @Test
    void removeLevel_ShouldReturnUpdatedLot() throws Exception {
        when(lotService.removeLevel(anyLong(), anyInt())).thenReturn(parkingLot);

        mockMvc.perform(delete("/api/v1/admin/lots/1/levels/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lot"));

        verify(lotService).removeLevel(1L, 2);
    }

    @Test
    void addSlot_ShouldReturnUpdatedLot() throws Exception {
        when(lotService.addSlot(anyLong(), anyInt(), any(ParkingSlotDTO.class))).thenReturn(parkingLot);

        mockMvc.perform(post("/api/v1/admin/lots/1/levels/2/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lot"));

        verify(lotService).addSlot(eq(1L), eq(2), any(ParkingSlotDTO.class));
    }

    @Test
    void removeSlot_ShouldReturnNoContent() throws Exception {
        doNothing().when(lotService).removeSlot(anyLong(), anyInt(), anyLong());

        mockMvc.perform(delete("/api/v1/admin/lots/1/levels/2/slots/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(lotService).removeSlot(1L, 2, 1L);
    }

    @Test
    void setSlotAvailability_ShouldReturnUpdatedLot() throws Exception {
        when(lotService.setSlotAvailability(anyLong(), anyInt(), anyLong(), anyBoolean())).thenReturn(parkingLot);

        mockMvc.perform(put("/api/v1/admin/lots/1/levels/2/slots/1/availability")
                        .param("available", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lot"));

        verify(lotService).setSlotAvailability(1L, 2, 1L, true);
    }

    @Test
    void getAllLots_ShouldReturnListOfLots() throws Exception {
        when(lotService.getAllLots()).thenReturn(List.of(parkingLot));

        mockMvc.perform(get("/api/v1/admin/lots")
                        .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Lot"));

        verify(lotService).getAllLots();
    }

    @Test
    void getAllLots_EmptyList_ShouldReturnEmptyList() throws Exception {
        when(lotService.getAllLots()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/admin/lots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(lotService).getAllLots();
    }

}