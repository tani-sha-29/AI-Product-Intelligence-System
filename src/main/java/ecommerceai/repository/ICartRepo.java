package ecommerceai.repository;

import ecommerceai.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepo extends JpaRepository<CartItem,Long> {
}
