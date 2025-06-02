package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.model.SlotType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ParkingSlotRepositoryTest {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindFirstAvailableSlotByType() {
        ParkingSlot slot = new ParkingSlot();
        slot.setType(SlotType.COMPACT);
        slot.setAvailable(true);
        entityManager.persist(slot);

        Optional<ParkingSlot> found = parkingSlotRepository.findFirstByTypeInAndAvailableTrue(List.of(SlotType.COMPACT, SlotType.LARGE));
        assertTrue(found.isPresent());
        assertEquals(SlotType.COMPACT, found.get().getType());
    }

    @Test
    void shouldFindByIdAndLevelNumberAndParkingLotId() {
        ParkingLot lot = new ParkingLot();
        lot.setName("Main Lot");
        entityManager.persist(lot);

        Level level = new Level();
        level.setNumber(2);
        level.setParkingLot(lot);
        entityManager.persist(level);

        ParkingSlot slot = new ParkingSlot();
        slot.setLevel(level);
        entityManager.persist(slot);

        Optional<ParkingSlot> found = parkingSlotRepository.findByIdAndLevelNumberAndLevelParkingLotId(
                slot.getId(), 2, lot.getId());

        assertTrue(found.isPresent());
    }

}
