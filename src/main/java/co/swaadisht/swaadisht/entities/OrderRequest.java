package co.swaadisht.swaadisht.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequest {
    private String address;

    private String city;

    private String state;

    private String pincode;

    private String paymentType;
}
