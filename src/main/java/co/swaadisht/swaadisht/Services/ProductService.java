package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    List<Product> getAllProducts();

    List<Product> getAllProductsWithIngredients();

    boolean deleteProduct(int id);

    public Product getProductById(int id);

    List<Product> getAllActiveProducts();
}
