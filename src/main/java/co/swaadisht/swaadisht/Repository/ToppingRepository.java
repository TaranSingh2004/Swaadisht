package co.swaadisht.swaadisht.Repository;

import co.swaadisht.swaadisht.entities.Toppings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToppingRepository extends JpaRepository<Toppings, Integer> {
    boolean existsByName(String name);

    List<Toppings> findByIsActiveTrue();
}
