package com.dms.smart_parking_system.domain.dto;

import com.dms.smart_parking_system.domain.model.VehicleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

}
