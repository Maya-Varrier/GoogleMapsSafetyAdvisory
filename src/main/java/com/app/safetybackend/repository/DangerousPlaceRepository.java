package com.app.safetybackend.repository;

import com.app.safetybackend.entity.DangerousPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DangerousPlaceRepository extends JpaRepository<DangerousPlace, Long> {

    // Fetch nearby places (simple version)
    List<DangerousPlace> findByLatitudeBetweenAndLongitudeBetween(
            double latMin, double latMax,
            double lonMin, double lonMax
    );
}