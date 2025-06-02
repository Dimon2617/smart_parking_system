package com.dms.smart_parking_system.domain.controller;

import com.dms.smart_parking_system.domain.dto.ParkingSlotDTO;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import com.dms.smart_parking_system.domain.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final ParkingLotService lotService;

    @PostMapping("/lots")
    public ResponseEntity<ParkingLot> createLot(@RequestParam String name) {
        return ResponseEntity.ok(lotService.createLot(name));
    }

    @DeleteMapping("/lots/{id}")
    public ResponseEntity<Void> deleteLot(@PathVariable Long id) {
        lotService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lots/{id}/levels")
    public ResponseEntity<ParkingLot> addLevel(@PathVariable Long id, @RequestParam int number) {
        return ResponseEntity.ok(lotService.addLevel(id, number));
    }

    @DeleteMapping("/lots/{id}/levels/{number}")
    public ResponseEntity<ParkingLot> removeLevel(@PathVariable Long id, @PathVariable int number) {
        return ResponseEntity.ok(lotService.removeLevel(id, number));
    }

    @PostMapping("/lots/{id}/levels/{number}/slots")
    public ResponseEntity<ParkingLot> addSlot(@PathVariable Long id, @PathVariable int number, @RequestBody ParkingSlotDTO slot) {
        return ResponseEntity.ok(lotService.addSlot(id, number, slot));
    }

    @DeleteMapping("/lots/{id}/levels/{number}/slots/{slotId}")
    public ResponseEntity<ParkingLot> removeSlot(@PathVariable Long id, @PathVariable int number, @PathVariable Long slotId) {
        lotService.removeSlot(id, number, slotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/lots/{id}/levels/{number}/slots/{slotId}/availability")
    public ResponseEntity<ParkingLot> setSlotAvailability(@PathVariable Long id, @PathVariable int number, @PathVariable Long slotId, @RequestParam boolean available) {
        return ResponseEntity.ok(lotService.setSlotAvailability(id, number, slotId, available));
    }

    @GetMapping("/lots")
    public ResponseEntity<Collection<ParkingLot>> getAllLots() {
        return ResponseEntity.ok(lotService.getAllLots());
    }

}
