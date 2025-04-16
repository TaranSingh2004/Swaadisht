package co.swaadisht.swaadisht.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String address;

    private String city;

    private String state;

    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    private Double distance;

    @OneToMany(mappedBy = "orderAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOrder> orders = new ArrayList<>();
}
