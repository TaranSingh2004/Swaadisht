package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.Toppings;

import java.util.List;

public interface  ToppingService {
    List<Toppings> getAllToppings();

    boolean existTopping(String name);

    Toppings saveTopping(Toppings toppings);

    boolean deleteTopping(int id);

    Toppings getToppingById(int id);

    List<Toppings> findAllByIds(List<Integer> selectedToppingIds);

    List<Toppings> getAllActiveToppings();
}
