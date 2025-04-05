package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.Category;

import java.util.List;

public interface CategoryServices {

    public Category saveCategory(Category category);

    public List<Category> getAllCategory();
}
