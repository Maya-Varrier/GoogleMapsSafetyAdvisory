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

    private final RestTemplate restTemplate;

    public GooglePlacesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CrowdRiskPlace> fetchNearby(double lat, double lng, int radius, String keyword) {

        List<CrowdRiskPlace> list = new ArrayList<>();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=" + radius
                + "&keyword=" + keyword
                + "&key=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("results")) {

            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) response.get("results");

            for (Map<String, Object> r : results) {

                Map geometry = (Map) r.get("geometry");
                if (geometry == null) continue;

                Map location = (Map) geometry.get("location");
                if (location == null) continue;

                CrowdRiskPlace c = new CrowdRiskPlace();
                c.setPlaceName((String) r.get("name"));
                c.setLatitude(((Number) location.get("lat")).doubleValue());
                c.setLongitude(((Number) location.get("lng")).doubleValue());
                c.setRiskScore(0.7);

                list.add(c);
            }
        }

        return list;
    }
}