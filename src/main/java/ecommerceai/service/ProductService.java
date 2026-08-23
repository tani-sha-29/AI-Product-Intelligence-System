package ecommerceai.service;

import ecommerceai.entity.Product;
import ecommerceai.exception.ProductNotFoundException;
import ecommerceai.repository.IProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    IProductRepo repo;
    public ProductService(IProductRepo repo){
        this.repo=repo;
    }

    @Override
    public List<Product> getProducts() {
        return repo.findAll();
    }

    @Override
    public String addProduct(Product product) {
        repo.save(product);
        return "Product added successfully";

    }

    @Override
    public String updateProduct(Product product) {
        Product existingProduct = repo.findById(product.getId())
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id : " + product.getId()));

        repo.save(existingProduct);
        return "Product updated successfully";
    }

    @Override
    public String deleteProduct(Long id) {
         repo.deleteById(id);
         return "Product deleted successfully";
    }

    @Override
    public Product getProductById(Long id) {
        System.out.println("ID = " + id);
        Product exist = repo.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id : " + id));

        return exist;
        }

    @Override
    public List<Product> getCheapestProducts() {
        return repo.getCheapestProducts();
    }

    @Override
    public List<Product>getProductByCategory(String category) {
        return repo.findByCategory(category);
    }

    @Override
    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice) {
        return repo.findByPriceBetween(minPrice,maxPrice);
    }

    @Override
    public List<Product> findByDescriptionIs(String keyword) {
        return repo.findByDescriptionIs(keyword);
    }


}
