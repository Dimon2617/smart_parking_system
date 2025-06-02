package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.ParkingTicket;
import com.dms.smart_parking_system.domain.model.Vehicle;
import com.dms.smart_parking_system.domain.model.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ParkingTicketRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Test
    void shouldFindFirstByLicensePlateAndExitTimeNull() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("AB1234");
        vehicle.setType(VehicleType.CAR);
        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicle(vehicle);
        ticket.setExitTime(null);
        entityManager.persist(ticket);

        Optional<ParkingTicket> found = parkingTicketRepository
                .findFirstByVehicleLicensePlateAndExitTimeIsNull("AB1234");

        assertTrue(found.isPresent());
    }

    @Test
    void shouldFindAllTicketsWithoutExitTime() {
        ParkingTicket ticket1 = new ParkingTicket();
        ticket1.setExitTime(null);
        entityManager.persist(ticket1);

        ParkingTicket ticket2 = new ParkingTicket();
        ticket2.setExitTime(null);
        entityManager.persist(ticket2);

        List<ParkingTicket> found = parkingTicketRepository.findAllByExitTimeIsNull();
        assertEquals(2, found.size());
    }

}
