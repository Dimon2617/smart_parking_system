package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByNumberAndParkingLotId(int number, Long lotId);
}
