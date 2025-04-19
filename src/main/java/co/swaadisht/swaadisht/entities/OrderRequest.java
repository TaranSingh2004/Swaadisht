package co.swaadisht.swaadisht.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequest {

    private OrderAddress orderAddress;
    private String couponCode;
    private String paymentType;
}
