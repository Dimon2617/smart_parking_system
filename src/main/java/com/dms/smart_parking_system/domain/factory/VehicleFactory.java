package com.dms.smart_parking_system.domain.factory;

import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleFactory {

    public Vehicle create(VehicleDTO vehicleDTO) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleDTO.getType());
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        return vehicle;
    }

}
