package com.app.safetybackend.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;
    private String destination;
    private String placeName;
    private String message;
    private double riskLevel;
    private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = Instant.now();
    }

    // GETTERS & SETTERS

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getRiskLevel() { return riskLevel; }
    public void setRiskLevel(double riskLevel) { this.riskLevel = riskLevel; }

    public Long getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
}