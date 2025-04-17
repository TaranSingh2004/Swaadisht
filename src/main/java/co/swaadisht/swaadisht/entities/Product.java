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

    private boolean customizable;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_available_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<CustomizationIngredient> availableIngredients = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_toppings",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private List<Toppings> availableToppings = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    public void clearCartReferences() {
        for (Cart cart : carts) {
            cart.setProduct(null);
            cart.getSelectedIngredients().clear();
            cart.getSelectedToppings().clear();
        }
        carts.clear();
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_size_mapping",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private List<ProductSize> availableSizes = new ArrayList<>();

    private boolean hasSizes = false;

}