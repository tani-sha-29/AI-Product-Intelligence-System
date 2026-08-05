package ecommerceai.repository;

import ecommerceai.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICartRepo extends JpaRepository<Cart,Long> {
    public Optional<Cart> findByUser(Long id);
}
