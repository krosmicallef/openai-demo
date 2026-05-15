package com.christophermicallef.openaidemo.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    public int getTemperature(String city) {
        if (city == null || city.trim().isEmpty()) {
            return 18;
        }
        String sanitizedCity = city
                .trim()
                .toUpperCase();
        return switch (sanitizedCity) {
            case "NEW YORK" -> 10;
            case "VALLETTA" -> 25;
            case "LONDON" -> 12;
            default -> 18;
        };
    }
}
