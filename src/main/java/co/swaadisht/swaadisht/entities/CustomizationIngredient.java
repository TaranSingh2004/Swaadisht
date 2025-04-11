package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customization_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

//    private double additionalPrice;

    private boolean isActive = true;

    @ManyToMany(mappedBy = "availableIngredients", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> products = new ArrayList<>();

    public void removeFromAllProducts() {
        for (Product product : new ArrayList<>(products)) {
            product.getAvailableIngredients().remove(this);
            products.remove(product);
        }
    }

}
