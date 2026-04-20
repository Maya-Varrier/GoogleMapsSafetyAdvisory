package com.app.safetybackend.scheduler;

import com.app.safetybackend.service.GooglePlacesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RiskDataScheduler {

    private final GooglePlacesService service;

    public RiskDataScheduler(GooglePlacesService service) {
        this.service = service;
    }

    // Runs every 30 minutes
    @Scheduled(fixedRate = 1800000)
    public void updateRiskData() {
        System.out.println("Refreshing crowd risk data...");

        // Example: Kochi location
        service.fetchFromRiskConfig(9.9312, 76.2673);
    }
}