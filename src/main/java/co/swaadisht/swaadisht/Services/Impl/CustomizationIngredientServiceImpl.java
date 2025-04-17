package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CustomizationIngredientRepository;
import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Services.CustomizationIngredientService;
import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomizationIngredientServiceImpl implements CustomizationIngredientService {
    @Autowired
    private CustomizationIngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<CustomizationIngredient> getAllActiveIngredients() {
        return ingredientRepository.findByIsActiveTrue();
    }

    @Override
    public boolean existIngredient(String name) {
        return ingredientRepository.existsByName(name);
    }

    @Override
    public CustomizationIngredient saveIngredient(CustomizationIngredient ingredient) {
        ingredientRepository.save(ingredient);
        return ingredient;
    }

    @Override
    @Transactional
    public boolean deleteIngredient(int id) {
        try {
            CustomizationIngredient ingredient = ingredientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

            // Remove this ingredient from all products first
            for (Product product : new ArrayList<>(ingredient.getProducts())) {
                product.getAvailableIngredients().remove(ingredient);
                productRepository.save(product);
            }

            // Clear the products list to maintain bidirectional relationship
            ingredient.getProducts().clear();

            // Now delete the ingredient
            ingredientRepository.delete(ingredient);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CustomizationIngredient getIngredientById(int id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    @Override
    public List<CustomizationIngredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public List<CustomizationIngredient> findAllByIds(List<Integer> selectedIngredientIds) {
        if (selectedIngredientIds == null || selectedIngredientIds.isEmpty()) {
            return Collections.emptyList();
        }
        return ingredientRepository.findAllById(selectedIngredientIds);
    }
}