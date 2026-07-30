package ecommerceai.service;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.CartResponse;

public interface ICartService {
    CartResponse getCarts();
    String addToCart(CartRequest cart);
    String removeFromCart(CartResponse cart);
}
