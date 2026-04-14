package com.app.safetybackend.service;

import com.app.safetybackend.entity.RiskConfig;
import com.app.safetybackend.repository.RiskConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskConfigService {

    private final RiskConfigRepository repo;

    public RiskConfigService(RiskConfigRepository repo) {
        this.repo = repo;
    }

    public List<RiskConfig> getAllConfigs() {
        return repo.findAll();
    }
}