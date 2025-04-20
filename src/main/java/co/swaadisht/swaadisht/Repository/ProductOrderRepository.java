package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository  extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(int userId);

    ProductOrder findByOrderIdAndUser(String orderId, User user);

    boolean existsByOrderAddressId(Integer addressId);

    List<ProductOrder> findByUserIdOrderByOrderDateDesc(int id);

    Optional<ProductOrder> findByIdAndUserId(Long orderId, int userId);

    List<ProductOrder> findByUserIdAndCancelledFalseOrderByOrderDateDesc(int id);

    ProductOrder findByOrderId(String orderId);

    ProductOrder findById(Long orderId);
}
