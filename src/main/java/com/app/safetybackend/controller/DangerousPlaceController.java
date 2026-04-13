package com.app.safetybackend.controller;

import com.app.safetybackend.entity.DangerousPlace;
import com.app.safetybackend.model.RoutePoint;
import com.app.safetybackend.service.DangerousPlaceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class DangerousPlaceController {

    private final DangerousPlaceService service;

    public DangerousPlaceController(DangerousPlaceService service) {
        this.service = service;
    }

    @GetMapping("/nearby")
    public List<DangerousPlace> getNearbyPlaces(
            @RequestParam double latMin,
            @RequestParam double latMax,
            @RequestParam double lonMin,
            @RequestParam double lonMax
    ) {
        return service.getPlacesNearRoute(latMin, latMax, lonMin, lonMax);
    }

    @PostMapping("/route-risk")
    public List<DangerousPlace> getPlacesAlongRoute(
            @RequestBody List<RoutePoint> routePoints
    ) {
        return service.getPlacesAlongRoute(routePoints);
    }
}