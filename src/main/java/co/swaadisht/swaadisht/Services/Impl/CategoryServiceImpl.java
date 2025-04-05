package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CategoryRepository;
import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryServices {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }
}
