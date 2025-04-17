package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {
    Optional<OrderAddress> findByIdAndUserId(Integer id, Integer userId);
}
