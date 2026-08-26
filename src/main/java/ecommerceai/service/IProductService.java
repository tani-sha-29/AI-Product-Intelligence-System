package ecommerceai.service;

import ecommerceai.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IProductService {

    // Paginated Methods
    Page<Product> getProducts(Pageable pageable);
    Page<Product> getProductByCategory(String category, Pageable pageable);
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product> findByDescriptionIs(String keyword, Pageable pageable);


    List<Product> getCheapestProducts();

    String addProduct(Product product);
    String updateProduct(Product product);
    String deleteProduct(Long id);
    Product getProductById(Long id);
}
