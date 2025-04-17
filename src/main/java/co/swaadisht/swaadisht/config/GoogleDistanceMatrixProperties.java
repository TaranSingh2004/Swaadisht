package co.swaadisht.swaadisht.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.distance-matrix")
public class GoogleDistanceMatrixProperties {

    private String apiKey;
    private String endpoint = "https://maps.googleapis.com/maps/api/distancematrix/json";
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
}
