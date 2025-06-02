package com.dms.smart_parking_system.domain.factory;

import com.dms.smart_parking_system.domain.dto.ParkingSlotDTO;
import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import org.springframework.stereotype.Component;

@Component
public class ParkingSlotFactory {

    public ParkingSlot create(ParkingSlotDTO parkingSlotDTO, Level level) {
        ParkingSlot slot = new ParkingSlot();
        slot.setType(parkingSlotDTO.getType());
        slot.setAvailable(parkingSlotDTO.isAvailable());
        slot.setLevel(level);
        return slot;
    }

}
