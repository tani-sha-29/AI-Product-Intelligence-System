package ecommerceai.repository;

import ecommerceai.entity.Cart;
import ecommerceai.entity.CartItem;
import ecommerceai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICartItemRepo extends JpaRepository<CartItem,Long> {
    public Optional<Product> findByProductId(Long id);
    public Optional<CartItem> findByCartIdAndProductId(Long id,Long pid);
    public List<CartItem> findByCart(Cart cart);
}
