package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Cart;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByProductAndUser(Product product, User user);

    List<Cart> findByUserId(Integer userId);

    void delete(Cart cart);

    boolean existsByProductId(int productId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.product.id = :productId")
    void deleteByProductId(@Param("productId") int productId);

    @Query("SELECT c FROM Cart c WHERE c.product.id = :productId")
    List<Cart> findAllByProductId(@Param("productId") int productId);
}
