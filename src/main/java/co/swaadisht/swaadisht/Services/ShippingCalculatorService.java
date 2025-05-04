package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.config.GoogleDistanceMatrixProperties;
import co.swaadisht.swaadisht.response.DistanceMatrixElement;
import co.swaadisht.swaadisht.response.DistanceMatrixResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ShippingCalculatorService {
    private final GoogleDistanceMatrixProperties properties;
    private final WebClient webClient;
    private Double lastCalculatedDistance; // Track last distance in meters

    public ShippingCalculatorService(GoogleDistanceMatrixProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getEndpoint())
                .build();
    }

    // Add this getter method
    public double getLastCalculatedDistance() {
        if (lastCalculatedDistance == null) {
            throw new IllegalStateException("No distance has been calculated yet");
        }
        return lastCalculatedDistance;
    }

    public DistanceMatrixResponse calculateDistance(String originPincode, String destinationPincode) {
        try {
            DistanceMatrixResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("units", "metric")
                            .queryParam("origins", originPincode + ",India")
                            .queryParam("destinations", destinationPincode + ",India")
                            .queryParam("key", properties.getApiKey())
                            .build())
                    .retrieve()
                    .bodyToMono(DistanceMatrixResponse.class)
                    .block();

            // Store the distance for later retrieval
            if (response != null &&
                    response.getRows() != null &&
                    !response.getRows().isEmpty() &&
                    response.getRows().get(0).getElements() != null &&
                    !response.getRows().get(0).getElements().isEmpty() &&
                    "OK".equals(response.getRows().get(0).getElements().get(0).getStatus())) {

                this.lastCalculatedDistance = (double) response.getRows().get(0)
                        .getElements().get(0)
                        .getDistance()
                        .getValue();
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance: " + e.getMessage());
        }
    }

    public double calculateShippingCharge(String originPincode, String destinationPincode) {
        DistanceMatrixResponse response = calculateDistance(originPincode, destinationPincode);

        if (lastCalculatedDistance == null) {
            throw new RuntimeException("Could not calculate distance");
        }

        double distanceInKm = lastCalculatedDistance / 1000.0;

        // Your shipping calculation logic
        return calculateChargeBasedOnDistance(distanceInKm);
    }

    private double calculateChargeBasedOnDistance(double distanceKm) {
        // Example pricing model - replace with your business logic
        if (distanceKm < 10) return 0;         // Local delivery
        if (distanceKm < 25) return 60;
        if (distanceKm < 40) return 110;
        if (distanceKm < 75) return 180;         // Nearby cities
        if (distanceKm < 100) return 205;       // Regional
        if (distanceKm > 100) return 270;
        return 0;// Long distance (₹1.5 per km beyond 200km)
    }
}