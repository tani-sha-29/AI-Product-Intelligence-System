package ecommerceai.repository;

import ecommerceai.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepo extends JpaRepository<Cart,Long> {
}
