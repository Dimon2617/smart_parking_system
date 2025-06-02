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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSlotRepository slotRepository;

    @Mock
    private ParkingTicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void checkIn_shouldWorkForAllVehicleTypes(VehicleType type) {
        String license = "TEST-" + type.name().substring(0, 1);
        VehicleDTO dto = new VehicleDTO(license, type);
        Vehicle vehicle = new Vehicle(1L, license,false, type);
        List<SlotType> expectedSlotType = mapVehicleTypeToSlotType(type);
        ParkingSlot slot = new ParkingSlot();
        slot.setId(1L);
        slot.setType(expectedSlotType.get(0));
        slot.setAvailable(true);

        when(ticketRepository.findByVehicleLicensePlateAndExitTimeIsNull(license)).thenReturn(Optional.empty());
        when(vehicleRepository.findByLicensePlate(license)).thenReturn(Optional.of(vehicle));
        when(slotRepository.findFirstByTypeInAndAvailableTrue(expectedSlotType)).thenReturn(Optional.of(slot));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ParkingTicket result = parkingService.checkIn(dto);

        assertEquals(vehicle, result.getVehicle());
        assertEquals(slot, result.getSlot());
        assertNotNull(result.getEntryTime());
        assertFalse(slot.isAvailable());
    }

    @Test
    void checkIn_shouldSucceed_whenVehicleNotAlreadyCheckedIn_andSlotAvailable() {
        VehicleDTO dto = new VehicleDTO("XYZ123", VehicleType.CAR);
        Vehicle vehicle = new Vehicle(1L, "XYZ123",false, VehicleType.CAR);
        ParkingSlot slot = new ParkingSlot(1L, SlotType.COMPACT, true, null);
        ParkingTicket ticket = new ParkingTicket();

        when(ticketRepository.findByVehicleLicensePlateAndExitTimeIsNull("XYZ123")).thenReturn(Optional.empty());
        when(vehicleRepository.findByLicensePlate("XYZ123")).thenReturn(Optional.of(vehicle));
        when(slotRepository.findFirstByTypeInAndAvailableTrue(mapVehicleTypeToSlotType(dto.getType()))).thenReturn(Optional.of(slot));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ParkingTicket result = parkingService.checkIn(dto);

        assertNotNull(result.getEntryTime());
        assertEquals(vehicle, result.getVehicle());
        assertEquals(slot, result.getSlot());
        assertFalse(slot.isAvailable());
    }

    @Test
    void checkIn_shouldThrow_whenVehicleAlreadyCheckedIn() {
        VehicleDTO dto = new VehicleDTO("XYZ123", VehicleType.CAR);
        when(ticketRepository.findByVehicleLicensePlateAndExitTimeIsNull("XYZ123"))
                .thenReturn(Optional.of(new ParkingTicket()));

        CommonException ex = assertThrows(CommonException.class, () -> parkingService.checkIn(dto));

        assertEquals(ErrorCode.ALREADY_CHECKED_IN, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldThrow_whenNoSlotAvailable() {
        VehicleDTO dto = new VehicleDTO("XYZ123", VehicleType.CAR);
        Vehicle vehicle = new Vehicle(1L, "XYZ123",false, VehicleType.CAR);

        when(ticketRepository.findByVehicleLicensePlateAndExitTimeIsNull("XYZ123")).thenReturn(Optional.empty());
        when(vehicleRepository.findByLicensePlate("XYZ123")).thenReturn(Optional.of(vehicle));
        when(slotRepository.findFirstByTypeInAndAvailableTrue(List.of(SlotType.COMPACT))).thenReturn(Optional.empty());

        CommonException ex = assertThrows(CommonException.class, () -> parkingService.checkIn(dto));

        assertEquals(ErrorCode.SLOT_NOT_AVAILABLE, ex.getErrorCode());
    }

    @Test
    void checkOut_shouldSucceed_whenActiveTicketExists() {
        String license = "XYZ123";
        Vehicle vehicle = new Vehicle(1L, license,false, VehicleType.CAR);
        ParkingSlot slot = new ParkingSlot(1L, SlotType.COMPACT, false, null);

        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicle(vehicle);
        ticket.setSlot(slot);
        ticket.setEntryTime(LocalDateTime.now().minusHours(2));

        when(ticketRepository.findFirstByVehicleLicensePlateAndExitTimeIsNull(license))
                .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(slotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FeeStrategy mockStrategy = mock(FeeStrategy.class);
        when(mockStrategy.calculateFee(any())).thenReturn(BigDecimal.valueOf(20.0));

        try (MockedStatic<FeeStrategyFactory> mockedStatic = mockStatic(FeeStrategyFactory.class)) {
            mockedStatic.when(() -> FeeStrategyFactory.getStrategy(vehicle.getType()))
                    .thenReturn(mockStrategy);

            ParkingTicket result = parkingService.checkOut(license);

            assertNotNull(result.getExitTime());
            assertEquals(BigDecimal.valueOf(20.0), result.getFee());
            assertTrue(result.getSlot().isAvailable());
        }
    }


    @Test
    void checkOut_shouldThrow_whenTicketNotFound() {
        when(ticketRepository.findFirstByVehicleLicensePlateAndExitTimeIsNull("XYZ123")).thenReturn(Optional.empty());

        CommonException ex = assertThrows(CommonException.class, () -> parkingService.checkOut("XYZ123"));

        assertEquals(ErrorCode.TICKET_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getActiveTickets_shouldReturnList() {
        ParkingTicket t1 = new ParkingTicket();
        ParkingTicket t2 = new ParkingTicket();

        when(ticketRepository.findAllByExitTimeIsNull()).thenReturn(List.of(t1, t2));

        List<ParkingTicket> result = parkingService.getActiveTickets();

        assertEquals(2, result.size());
    }

    private List<SlotType> mapVehicleTypeToSlotType(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> List.of(SlotType.MOTORCYCLE, SlotType.COMPACT, SlotType.LARGE);
            case CAR -> List.of(SlotType.COMPACT,SlotType.LARGE);
            case TRUCK -> List.of(SlotType.LARGE);
        };
    }

}
