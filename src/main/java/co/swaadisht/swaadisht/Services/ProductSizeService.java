package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.ProductSize;

import java.util.List;

public interface ProductSizeService {
    List<ProductSize> getAllProductSize();

    boolean existProductSize(String name);

    ProductSize saveProductSize(ProductSize productSize);

    boolean deleteProductSize(int id);

    ProductSize getProductSizeById(int id);

    List<ProductSize> findAllByIds(List<Integer> selectedProductSizeIds);

    List<ProductSize> getAllActiveProductSize();

    List<Product> getProductsBySizeId(int id);

    List<ProductSize> getAllProductSizesWithProducts();

    List<ProductSize> getAllActiveSizes();
}
