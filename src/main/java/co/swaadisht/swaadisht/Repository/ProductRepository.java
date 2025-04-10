package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.availableIngredients")
    List<Product> findAllWithIngredients();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.availableIngredients WHERE p.id = :id")
    Product findByIdWithIngredients(@Param("id") int id);
}
