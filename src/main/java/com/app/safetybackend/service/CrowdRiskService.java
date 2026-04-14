package com.app.safetybackend.service;

import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.repository.CrowdRiskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrowdRiskService {

    private final CrowdRiskRepository repo;

    public CrowdRiskService(CrowdRiskRepository repo) {
        this.repo = repo;
    }

    public void saveAll(List<CrowdRiskPlace> places) {
        repo.saveAll(places);
    }
}
