package com.app.safetybackend.service;

import com.app.safetybackend.entity.DangerousPlace;
import com.app.safetybackend.repository.DangerousPlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DangerousPlaceService {

    private final DangerousPlaceRepository repo;

    public DangerousPlaceService(DangerousPlaceRepository repo) {
        this.repo = repo;
    }

    public List<DangerousPlace> getPlacesNearRoute(
            double latMin, double latMax,
            double lonMin, double lonMax
    ) {
        return repo.findByLatitudeBetweenAndLongitudeBetween(
                latMin, latMax, lonMin, lonMax
        );
    }
}