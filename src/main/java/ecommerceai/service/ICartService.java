package ecommerceai.service;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.CartResponse;
import ecommerceai.entity.CartItem;

public interface ICartService {
    CartResponse getCarts();
    String addToCart(CartItem cart);
    String removeFromCart(CartRequest cart);
}
