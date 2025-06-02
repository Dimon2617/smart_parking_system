package com.dms.smart_parking_system.domain.dto;

import com.dms.smart_parking_system.domain.model.SlotType;
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
public class ParkingSlotDTO {

    @Enumerated(EnumType.STRING)
    private SlotType type;
    private boolean available = true;
}
