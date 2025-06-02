package com.dms.smart_parking_system.domain.controller;

import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.model.ParkingTicket;
import com.dms.smart_parking_system.domain.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking")
public class ParkingController {

    private final ParkingService service;

    @PostMapping("/checkin")
    public ResponseEntity<ParkingTicket> checkIn(@RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(service.checkIn(vehicleDTO));
    }

    @PostMapping("/checkout/{plate}")
    public ResponseEntity<ParkingTicket> checkOut(@PathVariable String plate) {
        return ResponseEntity.ok(service.checkOut(plate));
    }

    @GetMapping("/active")
    public ResponseEntity<Collection<ParkingTicket>> getActiveTickets() {
        return ResponseEntity.ok(service.getActiveTickets());
    }

}
