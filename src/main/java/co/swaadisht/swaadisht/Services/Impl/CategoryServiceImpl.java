package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CategoryRepository;
import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

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
    @Transactional
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

    @Override
    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories;
    }

}
