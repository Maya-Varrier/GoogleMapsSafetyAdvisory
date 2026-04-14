package com.app.safetybackend.controller;

import com.app.safetybackend.dto.SafetyResponse;
import com.app.safetybackend.dto.RoutePoint;
import com.app.safetybackend.service.SafetyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/safety")
public class SafetyController {

    private final SafetyService safetyService;

    public SafetyController(SafetyService safetyService) {
        this.safetyService = safetyService;
    }

    @PostMapping("/route")
    public SafetyResponse analyzeRoute(@RequestBody List<RoutePoint> points) {
        return safetyService.getRouteAnalysis(points);
    }
}
