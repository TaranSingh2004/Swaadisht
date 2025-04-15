package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository  extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(int userId);

    ProductOrder findByOrderIdAndUser(String orderId, User user);

    boolean existsByOrderAddressId(Integer addressId);
}
