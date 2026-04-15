package com.app.safetybackend.controller;

import com.app.safetybackend.service.GooglePlacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlacesController {

    @Autowired
    private GooglePlacesService service;

    @GetMapping("/fetch-places")
    public String fetch() {
        service.fetchAndSave(8.5241, 76.9366);
        return "Saved";
    }
}
