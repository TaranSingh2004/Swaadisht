package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Cart;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByProductAndUser(Product product, User user);

    List<Cart> findByUserId(Integer userId);

    void delete(Cart cart);
}
