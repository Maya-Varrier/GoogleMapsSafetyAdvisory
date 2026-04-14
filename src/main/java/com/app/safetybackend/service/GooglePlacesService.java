package com.app.safetybackend.service;

import com.app.safetybackend.entity.CrowdRiskPlace;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GooglePlacesService {

    private static final String API_KEY = "AIzaSyBtXMnSFNPtjfFxgg0T8sWNoBFY7Aqh5DM";

    public List<CrowdRiskPlace> fetchNearby(double lat, double lng) {

        List<CrowdRiskPlace> list = new ArrayList<>();

        // Example: call Google Places API using RestTemplate or WebClient

        // 🔥 MOCK LOGIC (replace with real API call)
        CrowdRiskPlace place = new CrowdRiskPlace();
        place.setPlaceName("Bar Example");
        place.setLatitude(lat);
        place.setLongitude(lng);
        place.setRiskScore(0.7);

        list.add(place);

        return list;
    }
}