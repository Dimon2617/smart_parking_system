package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    List<ParkingTicket> findAllByExitTimeIsNull();
    Optional<ParkingTicket> findByVehicleLicensePlateAndExitTimeIsNull(String licensePlate);
    Optional<ParkingTicket> findFirstByVehicleLicensePlateAndExitTimeIsNull(String licensePlate);
}
