package com.app.safetybackend.repository;

import com.app.safetybackend.entity.RiskConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskConfigRepository extends JpaRepository<RiskConfig, Long> {
}
