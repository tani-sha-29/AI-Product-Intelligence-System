package ecommerceai.service;

import ecommerceai.entity.Product;
import ecommerceai.exception.ProductNotFoundException;
import ecommerceai.repository.IProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {


    private final IProductRepo repo;

    public ProductService(IProductRepo repo){
        this.repo = repo;
    }

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public String addProduct(Product product) {
        repo.save(product);
        return "Product added successfully";
    }

    @Override
    public String updateProduct(Product product) {

        Product existingProduct = repo.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id : " + product.getId()));


        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        // Add any other specific field setters you have on your Product entity here

        // 3. Persist the changes
        repo.save(existingProduct);
        return "Product updated successfully";
    }

    @Override
    public String deleteProduct(Long id) {
        // Good practice to verify existence before throwing a generic database deletion exception
        if (!repo.existsById(id)) {
            throw new ProductNotFoundException("Cannot delete. Product not found with id : " + id);
        }
        repo.deleteById(id);
        return "Product deleted successfully";
    }

    @Override
    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id : " + id));
    }

    @Override
    public List<Product> getCheapestProducts() {
        return repo.getCheapestProducts();
    }

    @Override
    public Page<Product> getProductByCategory(String category, Pageable pageable) {
        return repo.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable) {
        return repo.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByDescriptionIs(String keyword, Pageable pageable) {
        return repo.findByDescriptionIs(keyword, pageable);
    }
}
