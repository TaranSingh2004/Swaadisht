package co.swaadisht.swaadisht.response;

import java.util.List;

public class DistanceMatrixRow {
    private List<DistanceMatrixElement> elements;

    // Getters and setters
    public List<DistanceMatrixElement> getElements() { return elements; }
    public void setElements(List<DistanceMatrixElement> elements) { this.elements = elements; }
}
