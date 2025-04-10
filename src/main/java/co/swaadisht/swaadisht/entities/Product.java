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
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_available_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<CustomizationIngredient> availableIngredients = new ArrayList<>();

    private boolean customizable;
}