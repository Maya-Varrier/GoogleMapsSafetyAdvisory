package com.app.safetybackend.controller;

import com.app.safetybackend.entity.RiskConfig;
import com.app.safetybackend.repository.RiskConfigRepository;
import com.app.safetybackend.service.RiskConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class RiskConfigController {

    private final RiskConfigService service;

    public RiskConfigController(RiskConfigService service) {
        this.service = service;
    }

    @GetMapping("/risk")
    public List<RiskConfig> getConfigs() {
        return service.getAllConfigs();
    }
}
