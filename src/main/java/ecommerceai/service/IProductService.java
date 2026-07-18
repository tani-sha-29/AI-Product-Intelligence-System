package ecommerceai.service;

import ecommerceai.entity.Product;

import java.util.List;

public interface IProductService {
    //get tourist by id
    List<Product> getProducts();
    String addProduct(Product product);
    String updateProduct(Product product);
    String deleteProduct(Long id);
    Product bygetId(Long id);
    //public List<Product> getCheapestProducts()

}
