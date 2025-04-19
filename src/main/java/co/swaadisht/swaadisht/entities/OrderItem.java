package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private ProductOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private boolean isCustomised;

    @ManyToMany
    @JoinTable(
            name = "order_item_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<CustomizationIngredient> selectedIngredients = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "order_item_toppings",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private List<Toppings> selectedToppings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "size_id")
    private ProductSize selectedSize;
}