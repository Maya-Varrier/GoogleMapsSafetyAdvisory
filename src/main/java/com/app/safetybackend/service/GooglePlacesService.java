package com.app.safetybackend.service;

import com.app.safetybackend.entity.CrowdRiskPlace;
import com.app.safetybackend.entity.RiskConfig;
import com.app.safetybackend.repository.CrowdRiskRepository;
import com.app.safetybackend.repository.RiskConfigRepository;
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
    private final RiskConfigRepository riskConfigRepository;

    // ✅ Constructor Injection
    @Autowired
    public GooglePlacesService(RestTemplate restTemplate,
                               CrowdRiskRepository repo,
                               RiskConfigRepository riskConfigRepository) {
        this.restTemplate = restTemplate;
        this.repo = repo;
        this.riskConfigRepository = riskConfigRepository;
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

    // ✅ Fetch + Save using risk_config table
    public void fetchFromRiskConfig(double lat, double lng) {

        List<RiskConfig> configs = riskConfigRepository.findAll();

        for (RiskConfig config : configs) {

            String keywordGroup = config.getKeyword();
            if (keywordGroup == null || keywordGroup.isEmpty()) continue;

            // Split using " OR "
            String[] keywords = keywordGroup.split("\\s+OR\\s+");

            for (String k : keywords) {

                String keyword = k.trim();
                if (keyword.isEmpty()) continue;

                fetchAndSaveByKeyword(lat, lng, keyword);
            }
        }
    }

    // ✅ Helper method for each keyword
    private void fetchAndSaveByKeyword(double lat, double lng, String keyword) {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=1500"
                + "&keyword=" + keyword
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

            // ✅ Prevent duplicates
            boolean exists = repo.existsByPlaceNameAndLatitudeAndLongitude(
                    name, placeLat, placeLng
            );

            if (!exists) {
                CrowdRiskPlace entity = new CrowdRiskPlace();
                entity.setPlaceName(name);
                entity.setLatitude(placeLat);
                entity.setLongitude(placeLng);
                entity.setRiskScore(riskScore);

                repo.save(entity); // ✅ IMPORTANT: saving enabled
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