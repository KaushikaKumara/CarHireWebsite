package com.kaushika.coreapi.repository;

import com.kaushika.coreapi.model.Vehicle;
import com.kaushika.coreapi.model.VehicleStatus;
import com.kaushika.coreapi.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByType(VehicleType type);
    boolean existsByLicensePlate(String licensePlate);
}