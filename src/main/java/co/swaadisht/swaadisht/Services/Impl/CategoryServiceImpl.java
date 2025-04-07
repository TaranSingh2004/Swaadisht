package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CategoryRepository;
import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    @Override
    public boolean deleteCategory(int id) {
        Category category=categoryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
//        Category category=categoryRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(category)){
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Boolean existCategory(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public Category getCategoryById(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        return category;
    }

}
