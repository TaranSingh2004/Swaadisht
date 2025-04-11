package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CustomizationIngredientRepository;
import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Repository.ToppingRepository;
import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import co.swaadisht.swaadisht.entities.Toppings;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IngredientToppingService {
    @Autowired
    private CustomizationIngredientRepository ingredientRepository;

    @Autowired
    private ToppingRepository toppingsRepository;

    @Autowired
    private ProductRepository productRepository;

    public void deleteIngredientAndRemoveFromProducts(int ingredientId) {
        CustomizationIngredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found"));

        // Remove from all products first
        ingredient.removeFromAllProducts();

        // Then delete the ingredient
        ingredientRepository.delete(ingredient);
    }

    public void deleteToppingAndRemoveFromProducts(int toppingId) {
        Toppings topping = toppingsRepository.findById(toppingId)
                .orElseThrow(() -> new ResourceNotFoundException("Topping not found"));

        // Remove from all products first
        topping.removeFromAllProducts();

        // Then delete the topping
        toppingsRepository.delete(topping);
    }
}
