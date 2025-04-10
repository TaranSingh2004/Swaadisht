package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "customization_toppings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Toppings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

//    private double additionalPrice;

    private boolean isActive = true;

    @ManyToMany(mappedBy = "availableToppings")
    private List<Product> products = new ArrayList<>();
}
