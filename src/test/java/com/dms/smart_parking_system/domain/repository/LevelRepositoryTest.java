package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class LevelRepositoryTest {

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByNumberAndParkingLotId() {
        ParkingLot lot = new ParkingLot();
        lot.setName("Lot A");
        entityManager.persist(lot);

        Level level = new Level();
        level.setNumber(1);
        level.setParkingLot(lot);
        entityManager.persist(level);

        Optional<Level> found = levelRepository.findByNumberAndParkingLotId(1, lot.getId());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getNumber());
    }

}
