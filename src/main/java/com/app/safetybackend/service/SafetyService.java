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
    private final GooglePlacesService googleService;

    public SafetyService(DangerousPlaceRepository repo1,
                         CrowdRiskRepository repo2, GooglePlacesService googleService) {
        this.repo1 = repo1;
        this.repo2 = repo2;
        this.googleService = googleService;
    }

    public SafetyResponse getRouteAnalysis(List<RoutePoint> routePoints) {

        // 🔥 FIX 1: Use Set to avoid duplicates automatically
        Set<DangerousPlace> result = new HashSet<>();

        double radius = 500; // meters

        if (routePoints == null || routePoints.isEmpty()) {
            return new SafetyResponse(new ArrayList<>(),
                    new SafetyResponse.Metadata(0, 0));
        }

        int limit = Math.min(routePoints.size(), 3);

        for (int i = 0; i < limit; i++) {
            RoutePoint p = routePoints.get(i);

            double lat = p.getLat();
            double lng = p.getLng();
            // ================= GOOGLE API FETCH (ADD HERE) =================
            Map<String, Object> response =
                    googleService.fetchNearbyPlaces(lat, lng, 500, "crowded OR market OR mall");

            if (response != null && response.containsKey("results")) {

                List<Map<String, Object>> results =
                        (List<Map<String, Object>>) response.get("results");

                for (Map<String, Object> r : results) {

                    Map<String, Object> geometry = (Map<String, Object>) r.get("geometry");
                    Map<String, Object> location = (Map<String, Object>) geometry.get("location");

                    String name = (String) r.get("name");
                    double placeLat = (double) location.get("lat");
                    double placeLng = (double) location.get("lng");

                    CrowdRiskPlace c = new CrowdRiskPlace();
                    c.setPlaceName(name);
                    c.setLatitude(placeLat);
                    c.setLongitude(placeLng);
                    c.setRiskScore(0.7);

                    if (!repo2.existsByPlaceNameAndLatitudeAndLongitude(name, placeLat, placeLng)) {
                        repo2.save(c);
                    }
                }
            }
            // ================= END GOOGLE FETCH =================
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

            // ===== TABLE 2 =====
            List<CrowdRiskPlace> db2 =
                    repo2.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            // 🔥 FIX 2: Correct logging
            System.out.println("DB1 size: " + db1.size() + " | DB2 size: " + db2.size());

            // ===== FILTER + ADD TABLE 1 =====
            for (DangerousPlace place : db1) {
                if (distance(lat, lng,
                        place.getLatitude(),
                        place.getLongitude()) <= radius) {

                    result.add(place);
                }
            }

            // ===== FILTER + ADD TABLE 2 =====
            for (CrowdRiskPlace c : db2) {
                if (distance(lat, lng,
                        c.getLatitude(),
                        c.getLongitude()) <= radius) {

                    result.add(convert(c));
                }
            }
        }

        List<DangerousPlace> finalList = new ArrayList<>(result);

        SafetyResponse.Metadata meta =
                new SafetyResponse.Metadata(finalList.size(), 0);

        return new SafetyResponse(finalList, meta);
    }

    // 🔥 OPTIONAL: Save Google API results into DB
    public void saveCrowdRiskPlace(CrowdRiskPlace place) {
        repo2.save(place);
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