package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Services.ProductService;
import co.swaadisht.swaadisht.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl  implements ProductService {

    @Autowired
    private ProductRepository productRepository;


    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
