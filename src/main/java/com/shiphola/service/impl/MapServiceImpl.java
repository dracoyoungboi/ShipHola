package com.shiphola.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiphola.dto.response.FeeCalculationResponse;
import com.shiphola.service.MapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * MapServiceImpl - Implementation cho MapService
 * Tích hợp OpenStreetMap Nominatim (geocoding) và OSRM (routing)
 */
@Service
public class MapServiceImpl implements MapService {

    @Value("${osm.nominatim.url:https://nominatim.openstreetmap.org}")
    private String nominatimUrl;

    @Value("${osm.osrm.url:http://router.project-osrm.org}")
    private String osrmUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MapServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String reverseGeocode(double lat, double lon) {
        try {
            // Use zoom=18 for street level detail, accept-language=vi for Vietnamese
            String url = String.format("%s/reverse?lat=%f&lon=%f&format=json&accept-language=vi&zoom=18&addressdetails=1",
                    nominatimUrl, lat, lon);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            // Priority 1: Use display_name (already formatted by Nominatim)
            if (root.has("display_name")) {
                String displayName = root.get("display_name").asText();
                // Remove country name if present
                displayName = displayName.replaceAll(",\\s*(Việt Nam|Vietnam)$", "");
                return displayName.trim();
            }

            // Priority 2: Build from address components
            if (root.has("address")) {
                JsonNode addr = root.get("address");
                StringBuilder formattedAddress = new StringBuilder();

                // Try multiple field names for flexibility
                String[] houseFields = {"house_number", "building", "house"};
                for (String field : houseFields) {
                    if (addr.has(field)) {
                        formattedAddress.append(addr.get(field).asText());
                        break;
                    }
                }

                // Try multiple field names for street/road
                String[] roadFields = {"road", "street", "pedestrian", "path", "cycleway"};
                for (String field : roadFields) {
                    if (addr.has(field)) {
                        if (formattedAddress.length() > 0) formattedAddress.append(", ");
                        formattedAddress.append(addr.get(field).asText());
                        break;
                    }
                }

                // Suburb/Village
                if (addr.has("suburb")) {
                    if (formattedAddress.length() > 0) formattedAddress.append(", ");
                    formattedAddress.append(addr.get("suburb").asText());
                } else if (addr.has("village")) {
                    if (formattedAddress.length() > 0) formattedAddress.append(", ");
                    formattedAddress.append(addr.get("village").asText());
                }

                // Ward
                if (addr.has("ward") || addr.has("city_district")) {
                    if (formattedAddress.length() > 0) formattedAddress.append(", ");
                    String ward = addr.has("ward") ? addr.get("ward").asText() : addr.get("city_district").asText();
                    formattedAddress.append(ward);
                }

                // District
                if (addr.has("district") || addr.has("county")) {
                    if (formattedAddress.length() > 0) formattedAddress.append(", ");
                    String district = addr.has("district") ? addr.get("district").asText() : addr.get("county").asText();
                    formattedAddress.append(district);
                }

                // City
                if (addr.has("city") || addr.has("state") || addr.has("province")) {
                    if (formattedAddress.length() > 0) formattedAddress.append(", ");
                    String city = addr.has("city") ? addr.get("city").asText() :
                                addr.has("state") ? addr.get("state").asText() :
                                addr.get("province").asText();
                    formattedAddress.append(city);
                }

                if (formattedAddress.length() > 0) {
                    return formattedAddress.toString();
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public double[] forwardGeocode(String address) {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String url = String.format("%s/search?q=%s&format=json&limit=1&countrycodes=vn",
                    nominatimUrl, encodedAddress);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("lat") && first.has("lon")) {
                    double lat = first.get("lat").asDouble();
                    double lon = first.get("lon").asDouble();
                    return new double[]{lat, lon};
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public FeeCalculationResponse calculateDistanceAndDuration(double lat1, double lon1, double lat2, double lon2) {
        try {
            // Request with alternatives=true to get both shortest and alternative routes
            String url = String.format("%s/route/v1/driving/%f,%f;%f,%f?alternatives=true&overview=false",
                    osrmUrl, lon1, lat1, lon2, lat2);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("routes") && root.get("routes").isArray()) {
                JsonNode routes = root.get("routes");

                // Get distances from all routes
                List<Double> distances = new ArrayList<>();
                for (JsonNode route : routes) {
                    if (route.has("distance")) {
                        // OSRM returns distance in meters
                        double distanceMeters = route.get("distance").asDouble();
                        distances.add(distanceMeters);
                    }
                }

                if (distances.isEmpty()) {
                    return null;
                }

                // Calculate average of shortest and longest
                double shortest = distances.stream().min(Double::compare).orElse(0.0);
                double longest = distances.stream().max(Double::compare).orElse(0.0);

                // Average distance in km
                double avgDistanceKm = ((shortest + longest) / 2.0) / 1000.0;
                // Round to 0.5km precision
                avgDistanceKm = Math.round(avgDistanceKm * 2.0) / 2.0;

                // Get duration from the first route (in seconds)
                double durationSeconds = 0;
                if (routes.get(0).has("duration")) {
                    durationSeconds = routes.get(0).get("duration").asDouble();
                }
                double durationMinutes = Math.ceil(durationSeconds / 60.0);

                FeeCalculationResponse result = new FeeCalculationResponse();
                result.setDistance(avgDistanceKm);
                result.setDuration((int) durationMinutes);
                result.setFee(calculateFee(avgDistanceKm));

                return result;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Double calculateFee(double distanceKm) {
        // Pricing rules based on distance
        if (distanceKm <= 1.5) return 12000.0;
        if (distanceKm <= 4) return 15000.0;
        if (distanceKm <= 5) return 18000.0;
        if (distanceKm <= 6) return 20000.0;
        if (distanceKm <= 7) return 25000.0;
        return null; // Over 7km - negotiable
    }

    @Override
    public FeeCalculationResponse calculateFeeFromAddresses(String pickupAddress, String deliveryAddress) {
        // Geocode both addresses
        double[] pickupCoords = forwardGeocode(pickupAddress);
        if (pickupCoords == null) {
            return null;
        }

        double[] deliveryCoords = forwardGeocode(deliveryAddress);
        if (deliveryCoords == null) {
            return null;
        }

        // Calculate distance and fee
        return calculateDistanceAndDuration(
                pickupCoords[0], pickupCoords[1],
                deliveryCoords[0], deliveryCoords[1]
        );
    }
}
