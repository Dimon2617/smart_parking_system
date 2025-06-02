package com.dms.smart_parking_system.domain.service;

import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.factory.VehicleFactory;
import com.dms.smart_parking_system.domain.model.Vehicle;
import com.dms.smart_parking_system.domain.model.VehicleType;
import com.dms.smart_parking_system.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleServiceTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleFactory vehicleFactory;

    @Mock
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewVehicle() {
        VehicleDTO dto = new VehicleDTO("ABC123", VehicleType.CAR);
        Vehicle vehicle = new Vehicle(1L, "ABC123",false, VehicleType.CAR);

        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.empty());
        when(vehicleFactory.create(dto)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle result = vehicleService.create(dto);

        assertEquals("ABC123", result.getLicensePlate());
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void testReturnExistingVehicle() {
        VehicleDTO dto = new VehicleDTO("ABC123", VehicleType.CAR);
        Vehicle vehicle = new Vehicle(1L, "ABC123",false, VehicleType.CAR);

        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.create(dto);

        assertEquals("ABC123", result.getLicensePlate());
        verify(vehicleRepository, never()).save(any());
    }

}
