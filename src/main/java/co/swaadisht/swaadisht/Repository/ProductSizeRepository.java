package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.ProductSize;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    boolean existsByName(String name);

    List<ProductSize> findByIsActiveTrue();

    @EntityGraph(attributePaths = {"products"})
    Optional<ProductSize> findWithProductsById(int id);

    @EntityGraph(attributePaths = {"products"})
    @Query("SELECT ps FROM ProductSize ps")
    List<ProductSize> findAllWithProducts();
}
