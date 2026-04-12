package com.app.safetybackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangerousPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeName;

    private double latitude;
    private double longitude;

    private double riskScore; // -2 to +2 (your logic)

    private String description;
}