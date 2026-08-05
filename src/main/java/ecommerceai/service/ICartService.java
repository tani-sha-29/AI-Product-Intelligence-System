package ecommerceai.service;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.CartResponse;

public interface ICartService {
    CartResponse getCart(Long UserId);
    String addToCart(CartRequest request);
    String removeFromCart(CartRequest request);
    String updateCart(CartRequest request);
    String clearCart(Long UserId);

}
