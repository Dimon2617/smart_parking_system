package com.dms.smart_parking_system.domain.service;

import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.factory.VehicleFactory;
import com.dms.smart_parking_system.domain.model.Vehicle;
import com.dms.smart_parking_system.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleFactory vehicleFactory;
    private final VehicleRepository vehicleRepository;

    public Vehicle create(VehicleDTO vehicleDTO) {
        return vehicleRepository.findByLicensePlate(vehicleDTO.getLicensePlate())
                .orElseGet(() -> vehicleRepository.save(vehicleFactory.create(vehicleDTO)));
    }

}
