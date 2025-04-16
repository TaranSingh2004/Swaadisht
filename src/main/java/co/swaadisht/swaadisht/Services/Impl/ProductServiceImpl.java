package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Services.ProductService;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl  implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;


    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllProductsWithIngredients() {
        return productRepository.findAllWithIngredients();
    }

    @Override
    @Transactional
    public boolean deleteProduct(int id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Option 1: Manual cleanup (recommended)
            product.clearCartReferences();
            productRepository.delete(product);

            // Option 2: Using direct queries (alternative)
            // cartRepository.deleteByProductId(id);
            // productRepository.deleteById(id);

            // Clear persistence context
            entityManager.flush();
            entityManager.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public Product getProductById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getAllActiveProducts() {
        List<Product> products = productRepository.findByStatusTrue();
        return products;
    }

    @Override
    public List<Product> getProductsByIds(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findAllById(productIds);
    }

}
