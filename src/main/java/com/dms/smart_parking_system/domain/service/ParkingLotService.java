package com.dms.smart_parking_system.domain.service;

import com.dms.smart_parking_system.domain.common.exception.CommonException;
import com.dms.smart_parking_system.domain.common.exception.ErrorCode;
import com.dms.smart_parking_system.domain.dto.ParkingSlotDTO;
import com.dms.smart_parking_system.domain.factory.LevelFactory;
import com.dms.smart_parking_system.domain.factory.ParkingSlotFactory;
import com.dms.smart_parking_system.domain.model.Level;
import com.dms.smart_parking_system.domain.model.ParkingLot;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.repository.LevelRepository;
import com.dms.smart_parking_system.domain.repository.ParkingLotRepository;
import com.dms.smart_parking_system.domain.repository.ParkingSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final LevelFactory levelFactory;
    private final LevelRepository levelRepository;
    private final ParkingSlotFactory parkingSlotFactory;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public ParkingLot createLot(String name) {
        ParkingLot lot = new ParkingLot();
        lot.setName(name);
        return parkingLotRepository.save(lot);
    }

    public void deleteLot(Long id) {
        if (parkingLotRepository.existsById(id)) {
            parkingLotRepository.deleteById(id);
        } else {
            throw new CommonException(ErrorCode.LOT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ParkingLot addLevel(Long lotId, int levelNumber) {
        if (levelRepository.findByNumberAndParkingLotId(levelNumber, lotId).isPresent()) {
            throw new CommonException(ErrorCode.LEVEL_ALREADY_EXIST, HttpStatus.CONFLICT);
        }

        ParkingLot lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new CommonException(ErrorCode.LOT_NOT_FOUND, HttpStatus.NOT_FOUND));

        lot.getLevels().add(levelFactory.create(levelNumber, lot));

        return parkingLotRepository.save(lot);
    }

    @Transactional
    public ParkingLot removeLevel(Long lotId, int levelNumber) {
        Level level = levelRepository.findByNumberAndParkingLotId(levelNumber, lotId)
                .orElseThrow(() -> new CommonException(ErrorCode.LEVEL_NOT_FOUND, HttpStatus.NOT_FOUND));

        levelRepository.delete(level);

        return parkingLotRepository.findById(lotId).orElseThrow(() -> new CommonException(ErrorCode.LOT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ParkingLot addSlot(Long lotId, int levelNumber, ParkingSlotDTO slotDTO) {
        Level level = levelRepository.findByNumberAndParkingLotId(levelNumber, lotId)
                .orElseThrow(() -> new CommonException(ErrorCode.LEVEL_NOT_FOUND, HttpStatus.NOT_FOUND));

        ParkingSlot slot = parkingSlotFactory.create(slotDTO, level);
        level.getSlots().add(slot);

        levelRepository.save(level);

        return parkingLotRepository.findById(lotId).orElseThrow(() -> new CommonException(ErrorCode.LOT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


    @Transactional
    public void removeSlot(Long lotId, int levelNumber, Long slotId) {
        ParkingSlot slot = parkingSlotRepository.findByIdAndLevelNumberAndLevelParkingLotId(slotId, levelNumber, lotId)
                .orElseThrow(() -> new CommonException(ErrorCode.SLOT_NOT_FOUND, HttpStatus.NOT_FOUND));

        parkingSlotRepository.delete(slot);
    }

    @Transactional
    public ParkingLot setSlotAvailability(Long lotId, int levelNumber, Long slotId, boolean available) {
        ParkingSlot slot = parkingSlotRepository.findByIdAndLevelNumberAndLevelParkingLotId(slotId, levelNumber, lotId)
                .orElseThrow(() -> new CommonException(ErrorCode.SLOT_NOT_FOUND, HttpStatus.NOT_FOUND));

        slot.setAvailable(available);

        parkingSlotRepository.save(slot);

        return parkingLotRepository.findById(lotId).orElseThrow(() -> new CommonException(ErrorCode.LOT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public Collection<ParkingLot> getAllLots() {
        return parkingLotRepository.findAll();
    }

}
