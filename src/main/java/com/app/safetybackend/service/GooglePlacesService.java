package com.app.safetybackend.service;

import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.repository.CrowdRiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GooglePlacesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final CrowdRiskRepository repo;

    // ✅ Constructor Injection (best practice)
    @Autowired
    public GooglePlacesService(RestTemplate restTemplate, CrowdRiskRepository repo) {
        this.restTemplate = restTemplate;
        this.repo = repo;
    }

    // ✅ Fetch only (no DB save)
    public List<CrowdRiskPlace> fetchNearby(double lat, double lng, int radius, String keyword) {

        List<CrowdRiskPlace> list = new ArrayList<>();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=" + radius
                + "&keyword=" + keyword
                + "&key=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("results")) return list;

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("results");

        for (Map<String, Object> r : results) {

            String name = (String) r.get("name");
            if (name == null) continue;

            Map<String, Object> geometry = (Map<String, Object>) r.get("geometry");
            if (geometry == null) continue;

            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            if (location == null) continue;

            if (location.get("lat") == null || location.get("lng") == null) continue;

            double placeLat = ((Number) location.get("lat")).doubleValue();
            double placeLng = ((Number) location.get("lng")).doubleValue();

            int riskScore = calculateRisk(r);

            CrowdRiskPlace c = new CrowdRiskPlace();
            c.setPlaceName(name);
            c.setLatitude(placeLat);
            c.setLongitude(placeLng);
            c.setRiskScore(riskScore);

            list.add(c);
        }

        return list;
    }

    // ✅ Fetch + Save into DB
    public void fetchAndSave(double lat, double lng) {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=1500"
                + "&type=restaurant"
                + "&key=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("results") == null) return;

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("results");

        for (Map<String, Object> place : results) {

            String name = (String) place.get("name");
            if (name == null) continue;

            Map<String, Object> geometry = (Map<String, Object>) place.get("geometry");
            if (geometry == null) continue;

            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            if (location == null) continue;

            if (location.get("lat") == null || location.get("lng") == null) continue;

            double placeLat = ((Number) location.get("lat")).doubleValue();
            double placeLng = ((Number) location.get("lng")).doubleValue();

            int riskScore = calculateRisk(place);

            // ✅ Prevent duplicate entries
            boolean exists = repo.existsByPlaceNameAndLatitudeAndLongitude(
                    name, placeLat, placeLng
            );

            if (!exists) {
                CrowdRiskPlace entity = new CrowdRiskPlace();
                entity.setPlaceName(name);
                entity.setLatitude(placeLat);
                entity.setLongitude(placeLng);
                entity.setRiskScore(riskScore);

                repo.save(entity);
            }
        }
    }

    // ✅ Risk calculation logic
    private int calculateRisk(Map<String, Object> place) {

        double rating = place.get("rating") != null
                ? ((Number) place.get("rating")).doubleValue()
                : 3.0;

        int userRatings = place.get("user_ratings_total") != null
                ? ((Number) place.get("user_ratings_total")).intValue()
                : 0;

        if (userRatings > 500) return 8;
        if (userRatings > 200) return 6;
        if (userRatings > 50) return 4;
        return 2;
    }
}