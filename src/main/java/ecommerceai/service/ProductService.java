package ecommerceai.service;

import ecommerceai.entity.Product;
import ecommerceai.repository.IProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        repo.save(product);
        return "Product updated successfully";
    }

    @Override
    public String deleteProduct(Long id) {
         repo.deleteById(id);
         return "Product deleted successfully";
    }

    @Override
    public Product bygetId(Long id) {
        Optional<Product> exist = repo.findById(id);
        if(exist.isPresent()){
            return exist.get();
        }
        return null;
        }




}
