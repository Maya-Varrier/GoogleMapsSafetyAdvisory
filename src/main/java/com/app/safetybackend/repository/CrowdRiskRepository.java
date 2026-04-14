package com.app.safetybackend.repository;

import com.app.safetybackend.entity.CrowdRiskPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrowdRiskRepository extends JpaRepository<CrowdRiskPlace, Long> {

    List<CrowdRiskPlace> findByLatitudeBetweenAndLongitudeBetween(
            double latMin, double latMax,
            double lonMin, double lonMax
    );
}