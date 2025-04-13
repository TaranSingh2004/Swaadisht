package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Cart;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByProductAndUser(Product product, User user);

}
