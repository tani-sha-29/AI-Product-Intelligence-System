package ecommerceai.repository;

import ecommerceai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepo extends JpaRepository<Product,Long> {
    List<Product> findByCategory(String category);
    @Query("Select p from Product p where p.price =(select MIN(p2.price) from Product p2)")
    List<Product>getCheapestProducts();
    List<Product>findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product>findByDescriptionIs(String keyword);

}
