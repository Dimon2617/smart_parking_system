package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
}
