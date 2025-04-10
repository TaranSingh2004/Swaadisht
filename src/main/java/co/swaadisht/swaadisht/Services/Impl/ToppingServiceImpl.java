package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ToppingRepository;
import co.swaadisht.swaadisht.Services.ToppingService;
import co.swaadisht.swaadisht.entities.Toppings;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ToppingServiceImpl implements ToppingService {

    @Autowired
    private ToppingRepository toppingRepository;

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
    public boolean deleteTopping(int id) {
        Toppings toppings = toppingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Topping not found"));
        if(!ObjectUtils.isEmpty(toppings)){
            toppingRepository.delete(toppings);
            return true;
        }
        return false;
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
