package com.app.safetybackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GooglePlacesService {

    private static final String API_KEY = "AIzaSyBtXMnSFNPtjfFxgg0T8sWNoBFY7Aqh5DM";

    public Map<String, Object> fetchNearbyPlaces(double lat, double lng, int radius, String keyword) {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=" + radius
                + "&keyword=" + keyword
                + "&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Map.class);
    }
}