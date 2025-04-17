package co.swaadisht.swaadisht.response;

import java.util.List;

public class DistanceMatrixResponse {
    private List<DistanceMatrixRow> rows;
    private String status;

    // Getters and setters
    public List<DistanceMatrixRow> getRows() { return rows; }
    public void setRows(List<DistanceMatrixRow> rows) { this.rows = rows; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}