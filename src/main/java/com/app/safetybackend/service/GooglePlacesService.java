package com.app.safetybackend.service;

import com.app.safetybackend.entity.CrowdRiskPlace;
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

    private final RestTemplate restTemplate = new RestTemplate();

    public List<CrowdRiskPlace> fetchNearby(double lat, double lng, int radius, String keyword) {

        List<CrowdRiskPlace> list = new ArrayList<>();

        try {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                    + "?location=" + lat + "," + lng
                    + "&radius=" + radius
                    + "&keyword=" + keyword
                    + "&key=" + apiKey;

            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("results")) return list;

            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) response.get("results");

            for (Map<String, Object> r : results) {

                String name = (String) r.get("name");

                Map geometry = (Map) r.get("geometry");
                Map location = (Map) geometry.get("location");

                double placeLat = ((Number) location.get("lat")).doubleValue();
                double placeLng = ((Number) location.get("lng")).doubleValue();

                CrowdRiskPlace c = new CrowdRiskPlace();
                c.setPlaceName(name);
                c.setLatitude(placeLat);
                c.setLongitude(placeLng);
                c.setRiskScore(0.7); // default

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}