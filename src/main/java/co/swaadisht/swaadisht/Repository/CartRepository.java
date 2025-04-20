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

    List<Cart> findByUserIdAndOrderedFalse(int id);

    void deleteByUserIdAndOrderedFalse(int id);

    @Query("SELECT c FROM Cart c WHERE c.product.id = :productId AND c.user.id = :userId " +
            "AND c.isCustomized = :isCustomized")
    List<Cart> findByProductAndUserAndCustomizationStatus(
            @Param("productId") Integer productId,
            @Param("userId") Integer userId,
            @Param("isCustomized") boolean isCustomized);

    @Query("SELECT c FROM Cart c WHERE c.product.id = :productId AND c.user.id = :userId " +
            "AND c.isCustomized = :isCustomized AND c.selectedSize.id = :sizeId")
    List<Cart> findByProductAndUserAndCustomizationStatusAndSize(
            @Param("productId") Integer productId,
            @Param("userId") Integer userId,
            @Param("isCustomized") boolean isCustomized,
            @Param("sizeId") Integer sizeId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.ordered = true")
    int deleteByUserIdAndOrderedTrue(@Param("userId") Integer userId);

//    List<Cart> findByUserAndOrderedFalse(User user);
//
//    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.product LEFT JOIN FETCH c.selectedIngredients LEFT JOIN FETCH c.selectedToppings WHERE c.user.id = :userId AND c.ordered = false")
//    List<Cart> findUserCartsWithDetails(@Param("userId") Integer userId);
//
//    List<Cart> findByUserIdAndOrderedFalse(int id);
}
