package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Column(unique = true)
    private String orderId; // Unique order reference

    private LocalDate orderDate;
    private Double price;
    private Double totalPrice;
    private Double discountAmount;
    private String couponCode;
    private double shippingCharges;
    private String status;
    private String paymentType;

    @ManyToOne
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_address_id") // No unique constraint here
    private OrderAddress orderAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @PrePersist
    public void generateOrderId() {
        if (this.orderId == null) {
            this.orderId = "ORD-" + System.currentTimeMillis() + "-" +
                    ThreadLocalRandom.current().nextInt(1000, 9999);
        }
    }
}