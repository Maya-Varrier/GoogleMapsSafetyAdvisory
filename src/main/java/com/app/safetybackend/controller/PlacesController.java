package com.app.safetybackend.controller;

import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.repository.CrowdRiskRepository;
import com.app.safetybackend.service.GooglePlacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlacesController {

    private final GooglePlacesService service;
    private final CrowdRiskRepository repo;

    // ✅ Correct constructor
    @Autowired
    public PlacesController(GooglePlacesService service, CrowdRiskRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping("/fetch-places")
    public List<CrowdRiskPlace> fetch(@RequestParam double lat, @RequestParam double lng) {
        service.fetchAndSave(lat, lng);
        return repo.findAll();
    }
}