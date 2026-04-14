package com.app.safetybackend.controller;

import com.app.safetybackend.dto.SafetyResponse;
import com.app.safetybackend.dto.RoutePoint;
import com.app.safetybackend.service.SafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/safety")
public class SafetyController {

    @Autowired
    private SafetyService service;

    @PostMapping("/route-analysis")
    public SafetyResponse analyze(@RequestBody List<RoutePoint> routePoints) {
        return service.getRouteAnalysis(routePoints);
    }
}
