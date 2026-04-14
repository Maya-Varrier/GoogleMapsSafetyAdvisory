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

        List<DangerousPlace> result = new ArrayList<>();
        double radius = 500; // meters

        for (RoutePoint p : routePoints) {

            double lat = p.getLat();
            double lng = p.getLng();

            double latDelta = radius / 111000.0;
            double lngDelta = radius / (111000.0 * Math.cos(Math.toRadians(lat)));

            double minLat = lat - latDelta;
            double maxLat = lat + latDelta;
            double minLng = lng - lngDelta;
            double maxLng = lng + lngDelta;

            // ===== TABLE 1 =====
            List<DangerousPlace> db1 =
                    repo1.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            for (DangerousPlace place : db1) {
                if (distance(lat, lng, place.getLatitude(), place.getLongitude()) <= radius) {
                    addUnique(result, place);
                }
            }

            // ===== TABLE 2 =====
            List<CrowdRiskPlace> db2 =
                    repo2.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            for (CrowdRiskPlace c : db2) {
                if (distance(lat, lng, c.getLatitude(), c.getLongitude()) <= radius) {
                    addUnique(result, convert(c));
                }
            }

            // 🔥 DEBUG LOG (ADD THIS)
            System.out.println("DB1: " + db1.size() + ", DB2: " + db2.size());
        }

        SafetyResponse.Metadata meta =
                new SafetyResponse.Metadata(result.size(), 0);

        return new SafetyResponse(result, meta);
    }

    private void addUnique(List<DangerousPlace> list, DangerousPlace newPlace) {

        for (DangerousPlace p : list) {
            if (p.getPlaceName() != null &&
                    p.getPlaceName().equalsIgnoreCase(newPlace.getPlaceName()) &&
                    Math.abs(p.getLatitude() - newPlace.getLatitude()) < 0.0001 &&
                    Math.abs(p.getLongitude() - newPlace.getLongitude()) < 0.0001) {

                return; // duplicate
            }
        }

        list.add(newPlace);
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