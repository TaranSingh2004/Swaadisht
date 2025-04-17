package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Repository.ProductSizeRepository;
import co.swaadisht.swaadisht.Services.ProductSizeService;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.ProductSize;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {
    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductSize> getAllProductSize() {
        return productSizeRepository.findAll();
    }

    @Override
    public boolean existProductSize(String name) {
        return productSizeRepository.existsByName(name);
    }

    @Override
    public ProductSize saveProductSize(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    public boolean deleteProductSize(int id) {
        try {
            ProductSize productSize = productSizeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product Size not found with id : " + id));

            for (Product product : new ArrayList<>(productSize.getProducts())) {
                product.getAvailableSizes().remove(productSize);
                productRepository.save(product);
            }
            productSize.getProducts().clear();
            productSizeRepository.delete(productSize);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ProductSize getProductSizeById(int id) {
        return productSizeRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductSize> findAllByIds(List<Integer> selectedProductSizeIds) {
        if(selectedProductSizeIds == null || selectedProductSizeIds.isEmpty()){
            return Collections.emptyList();
        }
        return productSizeRepository.findAllById(selectedProductSizeIds);
    }

    @Override
    public List<ProductSize> getAllActiveProductSize() {
        return productSizeRepository.findByIsActiveTrue();
    }

    @Override
    public List<Product> getProductsBySizeId(int sizeId) {
        return productSizeRepository.findWithProductsById(sizeId)
                .map(ProductSize::getProducts)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<ProductSize> getAllProductSizesWithProducts() {
        return productSizeRepository.findAllWithProducts();
    }

    @Override
    public List<ProductSize> getAllActiveSizes() {
        return productSizeRepository.findByIsActiveTrue();
    }
}
