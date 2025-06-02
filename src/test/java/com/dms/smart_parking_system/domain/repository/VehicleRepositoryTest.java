package com.dms.smart_parking_system.domain.repository;

import com.dms.smart_parking_system.domain.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void shouldFindByLicensePlate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("XYZ-987");
        entityManager.persist(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByLicensePlate("XYZ-987");

        assertTrue(found.isPresent());
        assertEquals("XYZ-987", found.get().getLicensePlate());
    }

}
