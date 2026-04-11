package com.app.safetybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Feedback {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Travel details
        private String origin;
        private String destination;

        // Unsafe area details
        private String unsafeLocationFrom;
        private String unsafeLocationTo;

        // Time when area is unsafe (store as String for now, can improve later)
        private String unsafeTime;

        // User description
        @Column(length = 1000)
        private String message;

        // Risk level (0–10)
        private int riskLevel;
        private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = Instant.now();
    }

}