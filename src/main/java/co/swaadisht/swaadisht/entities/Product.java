package co.swaadisht.swaadisht.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name; // Should match "title" in form or vice versa

    private String description;

    private boolean status; // Should match "isActive" in form

    private int stock;

    private int price;

    private int discount;

    private int discountPrice;

    @ManyToOne(fetch = FetchType.LAZY)  // Better performance with lazy loading
    @JoinColumn(name = "category_id")
    private Category category;

    private String productImage;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredients> ingredients = new ArrayList<>();*/
}