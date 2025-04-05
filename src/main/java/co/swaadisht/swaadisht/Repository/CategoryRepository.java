package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Integer> {

}
