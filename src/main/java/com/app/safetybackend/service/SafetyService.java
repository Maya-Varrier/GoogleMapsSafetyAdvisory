package com.app.safetybackend.service;

import com.app.safetybackend.entity.DangerousPlace;
import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.dto.RoutePoint;
import com.app.safetybackend.repository.DangerousPlaceRepository;
import com.app.safetybackend.repository.CrowdRiskRepository;
import com.app.safetybackend.dto.SafetyResponse;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SafetyService {

    private final DangerousPlaceRepository repo1;
    private final CrowdRiskRepository repo2;

    public SafetyService(DangerousPlaceRepository repo1,
                         CrowdRiskRepository repo2) {
        this.repo1 = repo1;
        this.repo2 = repo2;
    }

    public SafetyResponse getRouteAnalysis(List<RoutePoint> routePoints) {

        Set<DangerousPlace> result = new HashSet<>();

        double radius = 200; // meters

        for (RoutePoint p : routePoints) {

            double lat = p.getLat();
            double lng = p.getLng();

            // 🔹 Bounding box calculation
            double latDelta = radius / 111000.0;
            double lngDelta = radius / (111000.0 * Math.cos(Math.toRadians(lat)));

            double minLat = lat - latDelta;
            double maxLat = lat + latDelta;
            double minLng = lng - lngDelta;
            double maxLng = lng + lngDelta;

            // ================= DB TABLE 1 =================
            List<DangerousPlace> db1Candidates =
                    repo1.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            for (DangerousPlace place : db1Candidates) {
                if (distance(lat, lng, place.getLatitude(), place.getLongitude()) <= radius) {
                    result.add(place);
                }
            }

            // ================= DB TABLE 2 =================
            List<CrowdRiskPlace> db2Candidates =
                    repo2.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            for (CrowdRiskPlace c : db2Candidates) {
                if (distance(lat, lng, c.getLatitude(), c.getLongitude()) <= radius) {
                    result.add(convert(c));
                }
            }
        }

        List<DangerousPlace> finalList = new ArrayList<>(result);

        SafetyResponse.Metadata meta =
                new SafetyResponse.Metadata(finalList.size(), 0);

        return new SafetyResponse(finalList, meta);
    }

    // ================= CONVERTER =================
    private DangerousPlace convert(CrowdRiskPlace c) {
        DangerousPlace dp = new DangerousPlace();
        dp.setPlaceName(c.getPlaceName());
        dp.setLatitude(c.getLatitude());
        dp.setLongitude(c.getLongitude());
        dp.setDescription("CROWD");
        dp.setRiskScore(c.getRiskScore());
        return dp;
    }

    // ================= HAVERSINE =================
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}