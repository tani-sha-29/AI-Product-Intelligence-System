package ecommerceai.service;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.CartResponse;
import ecommerceai.entity.CartItem;
import ecommerceai.repository.ICartRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService{

    ICartRepo cartRepo;

    public CartService(ICartRepo cartRepo){
       this.cartRepo=cartRepo;
    }

    @Override
    public CartResponse getCarts() {
        List<CartItem> items = cartRepo.findAll();
        CartResponse cartResponse = new CartResponse();
        cartResponse.setItems(items);
        return cartResponse;
    }

    @Override
    public String addToCart(CartItem cart) {
        cartRepo.save(cart);
        return "Added to cart";
    }


    @Override
    public String removeFromCart(CartRequest cart) {
        Optional<CartItem> item = cartRepo.findById(cart.getProductId());
        cartRepo.deleteById(cart.getProductId());
        return "Item removed from cart";
    }
}
