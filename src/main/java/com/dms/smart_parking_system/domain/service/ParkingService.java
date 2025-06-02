package com.dms.smart_parking_system.domain.service;

import com.dms.smart_parking_system.domain.common.exception.CommonException;
import com.dms.smart_parking_system.domain.common.exception.ErrorCode;
import com.dms.smart_parking_system.domain.dto.VehicleDTO;
import com.dms.smart_parking_system.domain.model.ParkingSlot;
import com.dms.smart_parking_system.domain.model.ParkingTicket;
import com.dms.smart_parking_system.domain.model.SlotType;
import com.dms.smart_parking_system.domain.model.Vehicle;
import com.dms.smart_parking_system.domain.model.VehicleType;
import com.dms.smart_parking_system.domain.repository.ParkingSlotRepository;
import com.dms.smart_parking_system.domain.repository.ParkingTicketRepository;
import com.dms.smart_parking_system.domain.repository.VehicleRepository;
import com.dms.smart_parking_system.domain.stratagy.FeeStrategy;
import com.dms.smart_parking_system.domain.stratagy.FeeStrategyFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingTicketRepository ticketRepository;

    @Transactional
    public ParkingTicket checkIn(VehicleDTO vehicleDTO) {
        ticketRepository.findByVehicleLicensePlateAndExitTimeIsNull(vehicleDTO.getLicensePlate())
                .ifPresent(ticket -> {
                    throw new CommonException(ErrorCode.ALREADY_CHECKED_IN, HttpStatus.BAD_REQUEST,
                            List.of("Vehicle is already checked in."));
                });

        Vehicle vehicle = vehicleRepository.findByLicensePlate(vehicleDTO.getLicensePlate())
                .orElseGet(() -> vehicleService.create(vehicleDTO));

        List<SlotType> requiredSlotType = mapVehicleTypeToSlotType(vehicle.getType());
        ParkingSlot slot = slotRepository.findFirstByTypeInAndAvailableTrue(requiredSlotType)
                .orElseThrow(() -> new CommonException(ErrorCode.SLOT_NOT_AVAILABLE, HttpStatus.NOT_FOUND));

        slot.setAvailable(false);
        slotRepository.save(slot);

        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicle(vehicle);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setSlot(slot);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public ParkingTicket checkOut(String licensePlate) {
        ParkingTicket ticket = ticketRepository.findFirstByVehicleLicensePlateAndExitTimeIsNull(licensePlate)
                .orElseThrow(() -> new CommonException(ErrorCode.TICKET_NOT_FOUND, HttpStatus.NOT_FOUND, List.of("Active ticket not found")));

        ticket.setExitTime(LocalDateTime.now());
        Duration duration = Duration.between(ticket.getEntryTime(), ticket.getExitTime());

        FeeStrategy strategy = FeeStrategyFactory.getStrategy(ticket.getVehicle().getType());
        ticket.setFee(strategy.calculateFee(duration));

        ParkingSlot slot = ticket.getSlot();
        slot.setAvailable(true);
        slotRepository.save(slot);

        return ticketRepository.save(ticket);
    }

    public List<ParkingTicket> getActiveTickets() {
        return ticketRepository.findAllByExitTimeIsNull();
    }

    private List<SlotType> mapVehicleTypeToSlotType(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> List.of(SlotType.MOTORCYCLE, SlotType.COMPACT, SlotType.LARGE);
            case CAR -> List.of(SlotType.COMPACT,SlotType.LARGE);
            case TRUCK -> List.of(SlotType.LARGE);
        };
    }

}
