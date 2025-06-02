package com.dms.smart_parking_system.domain.factory;

import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import org.springframework.stereotype.Component;

@Component
public class LevelFactory {

    public Level create(int number, ParkingLot parkingLot) {
        Level level = new Level();
        level.setNumber(number);
        level.setParkingLot(parkingLot);
        return level;
    }

}
