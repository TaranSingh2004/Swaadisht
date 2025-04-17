package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Repository.ToppingRepository;
import co.swaadisht.swaadisht.Services.ToppingService;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.Toppings;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ToppingServiceImpl implements ToppingService {

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Toppings> getAllToppings() {
        return toppingRepository.findAll();
    }

    @Override
    public boolean existTopping(String name) {
        return toppingRepository.existsByName(name);
    }

    @Override
    public Toppings saveTopping(Toppings toppings) {
        toppingRepository.save(toppings);
        return toppings;
    }

    @Override
    @Transactional
    public boolean deleteTopping(int id) {
        try {
            Toppings topping = toppingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Topping not found with id: " + id));

            // Remove this topping from all products first
            for (Product product : new ArrayList<>(topping.getProducts())) {
                product.getAvailableToppings().remove(topping);
                productRepository.save(product);
            }

            // Clear the products list to maintain bidirectional relationship
            topping.getProducts().clear();

            // Now delete the topping
            toppingRepository.delete(topping);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Toppings getToppingById(int id) {
        return toppingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Toppings> findAllByIds(List<Integer> selectedToppingIds) {
        if(selectedToppingIds == null || selectedToppingIds.isEmpty()){
            return Collections.emptyList();
        }
        return toppingRepository.findAllById(selectedToppingIds);
    }

    @Override
    public List<Toppings> getAllActiveToppings() {
        return toppingRepository.findByIsActiveTrue();
    }
}
