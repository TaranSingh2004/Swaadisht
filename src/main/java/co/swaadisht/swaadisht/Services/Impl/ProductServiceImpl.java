package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Services.ProductService;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl  implements ProductService {

    @Autowired
    private ProductRepository productRepository;


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
    public boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("product not found"));
        if(!ObjectUtils.isEmpty(product)){
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Transactional
    public Product getProductById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
