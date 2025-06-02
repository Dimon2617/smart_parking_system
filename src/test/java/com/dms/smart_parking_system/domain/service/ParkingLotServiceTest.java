package com.dms.smart_parking_system.domain.service;

import com.dms.smart_parking_system.domain.common.exception.CommonException;
import com.dms.smart_parking_system.domain.common.exception.ErrorCode;
import com.dms.smart_parking_system.domain.dto.ParkingSlotDTO;
import com.dms.smart_parking_system.domain.factory.LevelFactory;
import com.dms.smart_parking_system.domain.factory.ParkingSlotFactory;
import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.model.SlotType;
import com.dms.smart_parking_system.domain.repository.LevelRepository;
import com.dms.smart_parking_system.domain.repository.ParkingLotRepository;
import com.dms.smart_parking_system.domain.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParkingLotServiceTest {

    @InjectMocks
    private ParkingLotService parkingLotService;

    @Mock
    private LevelFactory levelFactory;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private ParkingSlotFactory parkingSlotFactory;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLot_shouldSaveAndReturnLot() {
        ParkingLot lot = new ParkingLot();
        lot.setName("Lot A");

        when(parkingLotRepository.save(any())).thenReturn(lot);

        ParkingLot result = parkingLotService.createLot("Lot A");

        assertEquals("Lot A", result.getName());
        verify(parkingLotRepository).save(any());
    }

    @Test
    void deleteLot_shouldDeleteIfExists() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        parkingLotService.deleteLot(1L);
        verify(parkingLotRepository).deleteById(1L);
    }

    @Test
    void deleteLot_shouldThrowIfNotExists() {
        when(parkingLotRepository.existsById(1L)).thenReturn(false);
        assertThrows(CommonException.class, () -> parkingLotService.deleteLot(1L));
    }

    @Test
    void addLevel_shouldAddNewLevel() {
        Long lotId = 1L;
        int levelNumber = 0;
        ParkingLot lot = new ParkingLot();
        lot.setId(lotId);
        lot.setLevels(new ArrayList<>());
        Level level = new Level();
        level.setParkingLot(lot);
        level.setNumber(levelNumber);

        when(levelRepository.findByNumberAndParkingLotId(levelNumber, lotId)).thenReturn(Optional.empty());
        when(parkingLotRepository.findById(lotId)).thenReturn(Optional.of(lot));
        when(levelFactory.create(levelNumber, lot)).thenReturn(level);
        when(parkingLotRepository.save(any())).thenReturn(lot);

        ParkingLot result = parkingLotService.addLevel(lotId, levelNumber);

        assertTrue(result.getLevels().contains(level));
    }

    @Test
    void addLevel_shouldThrowIfAlreadyExists() {
        when(levelRepository.findByNumberAndParkingLotId(0, 1L)).thenReturn(Optional.of(new Level()));
        assertThrows(CommonException.class, () -> parkingLotService.addLevel(1L, 0));
    }

    @Test
    void removeLevel_shouldDeleteLevelAndReturnLot() {
        Long lotId = 1L;
        int levelNumber = 2;

        ParkingLot lot = new ParkingLot();
        lot.setId(lotId);

        Level level = new Level();
        level.setId(100L);
        level.setNumber(levelNumber);
        level.setParkingLot(lot);

        when(levelRepository.findByNumberAndParkingLotId(levelNumber, lotId)).thenReturn(Optional.of(level));
        when(parkingLotRepository.findById(lotId)).thenReturn(Optional.of(lot));

        ParkingLot result = parkingLotService.removeLevel(lotId, levelNumber);

        verify(levelRepository).delete(level);
        verify(parkingLotRepository).findById(lotId);
        assertEquals(lotId, result.getId());
    }


    @Test
    void addSlot_shouldAddSlotToLevel() {
        Long lotId = 1L;
        int levelNumber = 0;
        ParkingSlotDTO dto = new ParkingSlotDTO(SlotType.COMPACT, true);

        Level level = new Level();
        level.setNumber(levelNumber);
        level.setSlots(new ArrayList<>());

        ParkingLot lot = new ParkingLot();
        lot.setId(lotId);

        ParkingSlot slot = new ParkingSlot();
        slot.setAvailable(true);
        slot.setType(SlotType.COMPACT);

        when(levelRepository.findByNumberAndParkingLotId(levelNumber, lotId)).thenReturn(Optional.of(level));
        when(parkingSlotFactory.create(dto, level)).thenReturn(slot);
        when(parkingLotRepository.findById(lotId)).thenReturn(Optional.of(lot));

        ParkingLot result = parkingLotService.addSlot(lotId, levelNumber, dto);

        assertTrue(level.getSlots().contains(slot));
        assertEquals(lotId, result.getId());
    }

    @Test
    void removeSlot_shouldDeleteSlot() {
        Long lotId = 1L;
        int levelNumber = 2;
        Long slotId = 99L;
        ParkingSlot slot = new ParkingSlot();
        slot.setId(slotId);

        when(parkingSlotRepository.findByIdAndLevelNumberAndLevelParkingLotId(slotId, levelNumber, lotId))
                .thenReturn(Optional.of(slot));

        parkingLotService.removeSlot(lotId, levelNumber, slotId);
        verify(parkingSlotRepository).delete(slot);
    }

    @Test
    void setSlotAvailability_shouldUpdateSlot() {
        Long lotId = 1L;
        int levelNumber = 0;
        Long slotId = 22L;
        ParkingSlot slot = new ParkingSlot();
        slot.setId(slotId);
        slot.setAvailable(false);

        ParkingLot lot = new ParkingLot();
        lot.setId(lotId);

        when(parkingSlotRepository.findByIdAndLevelNumberAndLevelParkingLotId(slotId, levelNumber, lotId))
                .thenReturn(Optional.of(slot));
        when(parkingLotRepository.findById(lotId)).thenReturn(Optional.of(lot));

        ParkingLot result = parkingLotService.setSlotAvailability(lotId, levelNumber, slotId, true);

        assertTrue(slot.isAvailable());
        assertEquals(lotId, result.getId());
    }

    @Test
    void getAllLots_shouldReturnList() {
        List<ParkingLot> lots = List.of(new ParkingLot(), new ParkingLot());
        when(parkingLotRepository.findAll()).thenReturn(lots);
        Collection<ParkingLot> result = parkingLotService.getAllLots();
        assertEquals(2, result.size());
    }

}
