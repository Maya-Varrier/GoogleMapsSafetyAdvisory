package com.app.safetybackend.controller;

import com.app.safetybackend.entity.RiskConfig;
import com.app.safetybackend.repository.RiskConfigRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-config")
public class RiskConfigController {

    private final RiskConfigRepository repo;

    public RiskConfigController(RiskConfigRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<RiskConfig> getAllConfigs() {
        return repo.findAll();
    }
}
