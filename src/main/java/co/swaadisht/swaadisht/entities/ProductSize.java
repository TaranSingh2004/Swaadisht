package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_size")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    private boolean isActive = true;

    @ManyToMany(mappedBy = "availableSizes", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> products = new ArrayList<>();

    private double price;

    private String description;

    public void addProduct(Product product) {
        this.products.add(product);
        product.getAvailableSizes().add(this); // Update the other side
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        product.getAvailableSizes().remove(this); // Update the other side
    }
}
