package ecommerceai.repository;

import ecommerceai.entity.Cart;
import ecommerceai.entity.CartItem;
import ecommerceai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICartItemRepo extends JpaRepository<CartItem,Long> {
    public Optional<Product> findByProduct(Long id);
    public Optional<CartItem> findByCartAndProduct(Long id,Long pid);
    public List<CartItem> findByCart(Cart cart);
}
