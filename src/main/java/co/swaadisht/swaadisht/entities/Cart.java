package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    private Integer quantity;

    @Transient
    private Double totalPrice;

    @Transient
    private Double totalOrderPrice;

    private boolean isCustomized = false;

    private boolean ordered= false;

    @ManyToMany
    @JoinTable(
            name = "cart_selected_ingredients",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<CustomizationIngredient> selectedIngredients = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "cart_selected_toppings",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private List<Toppings> selectedToppings = new ArrayList<>();
}
