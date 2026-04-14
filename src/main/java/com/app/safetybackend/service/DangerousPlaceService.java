package com.app.safetybackend.service;

import com.app.safetybackend.entity.DangerousPlace;
import com.app.safetybackend.repository.DangerousPlaceRepository;
import com.app.safetybackend.dto.RoutePoint;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DangerousPlaceService {

    private final DangerousPlaceRepository repo;

    public DangerousPlaceService(DangerousPlaceRepository repo) {
        this.repo = repo;
    }

    // 🔥 NEW METHOD (USED BY YOUR API)
    public List<DangerousPlace> getPlacesAlongRoute(List<RoutePoint> routePoints) {

        Set<DangerousPlace> result = new HashSet<>();

        double range = 0.05; // ~5km radius

        for (RoutePoint point : routePoints) {

            List<DangerousPlace> nearby = repo
                    .findByLatitudeBetweenAndLongitudeBetween(
                            point.getLat() - range,
                            point.getLat() + range,
                            point.getLng() - range,
                            point.getLng() + range
                    );

            result.addAll(nearby);
        }

        return new ArrayList<>(result);
    }

    // ✅ KEEP THIS (used by /nearby API if needed)
    public List<DangerousPlace> getPlacesNearRoute(
            double latMin, double latMax,
            double lonMin, double lonMax
    ) {
        return repo.findByLatitudeBetweenAndLongitudeBetween(
                latMin, latMax, lonMin, lonMax
        );
    }

    public List<DangerousPlace> getAllPlaces() {
        return repo.findAll();
    }
}