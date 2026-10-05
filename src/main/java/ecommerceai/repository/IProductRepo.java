package ecommerceai.repository;

import ecommerceai.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepo extends JpaRepository<Product,Long> {
    Page<Product> findByCategory(String category, Pageable pageable);
    @Query("Select p from Product p where p.price =(select MIN(p2.price) from Product p2)")
    List<Product>getCheapestProducts();
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product>findByDescriptionIs(String keyword, Pageable pageable);
    @Query(value = "SELECT * FROM products ORDER BY embedding <=> CAST(:queryEmbedding AS vector) LIMIT :limit", nativeQuery = true)
    List<Product> findSimilarProducts(@Param("queryEmbedding") String queryEmbedding, @Param("limit") int limit);
}
