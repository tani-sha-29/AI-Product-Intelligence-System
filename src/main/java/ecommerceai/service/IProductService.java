package ecommerceai.service;

import ecommerceai.entity.Product;

import java.util.List;

public interface IProductService {

    List<Product> getProducts();
    String addProduct(Product product);
    String updateProduct(Product product);
    String deleteProduct(Long id);
    Product getProductById(Long id);
    List<Product> getCheapestProducts();
    List<Product>getProductByCategory(String category);
    List<Product>findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product>findByDescriptionIs(String keyword);



}
