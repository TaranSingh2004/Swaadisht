package co.swaadisht.swaadisht.response;

public class DistanceMatrixElement {
    private DistanceValue distance;
    private DurationValue duration;
    private String status;

    // Getters and setters
    public DistanceValue getDistance() { return distance; }
    public void setDistance(DistanceValue distance) { this.distance = distance; }
    public DurationValue getDuration() { return duration; }
    public void setDuration(DurationValue duration) { this.duration = duration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
