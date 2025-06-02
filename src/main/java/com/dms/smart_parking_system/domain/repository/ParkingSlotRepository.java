package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.model.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findFirstByTypeInAndAvailableTrue(List<SlotType> types);
    Optional<ParkingSlot> findByIdAndLevelNumberAndLevelParkingLotId(Long slotId, int levelNumber, Long lotId);
}
