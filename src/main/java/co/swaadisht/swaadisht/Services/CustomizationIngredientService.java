package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.CustomizationIngredient;

import java.util.List;

public interface CustomizationIngredientService {
    CustomizationIngredient saveIngredient(CustomizationIngredient ingredient);

    boolean deleteIngredient(int id);

    CustomizationIngredient getIngredientById(int id);

    List<CustomizationIngredient> getAllIngredients();

    List<CustomizationIngredient> findAllByIds(List<Integer> selectedIngredientIds);

    List<CustomizationIngredient> getAllActiveIngredients();

    boolean existIngredient(String name);
}
