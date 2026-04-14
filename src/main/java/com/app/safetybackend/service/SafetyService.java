package com.app.safetybackend.service;

import com.app.safetybackend.entity.DangerousPlace;
import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.dto.RoutePoint;
import com.app.safetybackend.repository.DangerousPlaceRepository;
import com.app.safetybackend.repository.CrowdRiskRepository;
import com.app.safetybackend.dto.SafetyResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SafetyService {

    private final DangerousPlaceRepository repo1;
    private final CrowdRiskRepository repo2;
    private final GooglePlacesService googleService;

    public SafetyService(DangerousPlaceRepository repo1,
                         CrowdRiskRepository repo2,
                         GooglePlacesService googleService) {
        this.repo1 = repo1;
        this.repo2 = repo2;
        this.googleService = googleService;
    }

    @Transactional
    public SafetyResponse getRouteAnalysis(List<RoutePoint> routePoints) {

        Set<DangerousPlace> result = new HashSet<>();
        double radius = 500;

        if (routePoints == null || routePoints.isEmpty()) {
            return new SafetyResponse(
                    new ArrayList<>(),
                    new SafetyResponse.Metadata(0, 0)
            );
        }

        int limit = Math.min(routePoints.size(), 3); // reduce API calls

        for (int i = 0; i < limit; i++) {

            RoutePoint p = routePoints.get(i);
            double lat = p.getLat();
            double lng = p.getLng();

            // ================= GOOGLE API =================
            List<CrowdRiskPlace> googleResults =
                    googleService.fetchNearby(lat, lng, 500,
                            "crowded OR market OR mall");

            if (googleResults != null && !googleResults.isEmpty()) {

                for (CrowdRiskPlace c : googleResults) {

                    // Prevent duplicates in DB
                    if (!repo2.existsByPlaceNameAndLatitudeAndLongitude(
                            c.getPlaceName(),
                            c.getLatitude(),
                            c.getLongitude())) {

                        repo2.save(c);
                    }
                }
            }

            // ================= BOUNDING BOX =================
            double latDelta = radius / 111000.0;
            double lngDelta = radius / (111000.0 * Math.cos(Math.toRadians(lat)));

            double minLat = lat - latDelta;
            double maxLat = lat + latDelta;
            double minLng = lng - lngDelta;
            double maxLng = lng + lngDelta;

            // ================= DB TABLE 1 =================
            List<DangerousPlace> db1 =
                    repo1.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            // ================= DB TABLE 2 =================
            List<CrowdRiskPlace> db2 =
                    repo2.findByLatitudeBetweenAndLongitudeBetween(
                            minLat, maxLat, minLng, maxLng
                    );

            System.out.println("DB1 size: " + db1.size() +
                    " | DB2 size: " + db2.size());

            // ================= FILTER TABLE 1 =================
            for (DangerousPlace place : db1) {
                if (distance(lat, lng,
                        place.getLatitude(),
                        place.getLongitude()) <= radius) {

                    result.add(place);
                }
            }

            // ================= FILTER TABLE 2 =================
            for (CrowdRiskPlace c : db2) {
                if (distance(lat, lng,
                        c.getLatitude(),
                        c.getLongitude()) <= radius) {

                    result.add(convert(c));
                }
            }
        }

        List<DangerousPlace> finalList = new ArrayList<>(result);

        return new SafetyResponse(
                finalList,
                new SafetyResponse.Metadata(finalList.size(), 0)
        );
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