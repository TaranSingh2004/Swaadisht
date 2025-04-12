package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, Integer> {
    public Boolean existsByName(String name);

    Category findByName(String categoryName);

    public List<Category> findByIsActiveTrue();
}
