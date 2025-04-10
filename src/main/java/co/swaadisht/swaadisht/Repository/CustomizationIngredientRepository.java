package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomizationIngredientRepository extends JpaRepository<CustomizationIngredient, Integer> {
    List<CustomizationIngredient> findByIsActiveTrue();

    boolean existsByName(String name);
}

