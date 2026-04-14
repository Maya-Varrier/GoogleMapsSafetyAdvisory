package com.app.safetybackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_config")
public class RiskConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private int radius;
    private String category;
    private String reason;
    private double riskScore;

    // Getters
    public String getKeyword() { return keyword; }
    public int getRadius() { return radius; }
    public String getCategory() { return category; }
    public String getReason() { return reason; }
    public double getRiskScore() { return riskScore; }
}
