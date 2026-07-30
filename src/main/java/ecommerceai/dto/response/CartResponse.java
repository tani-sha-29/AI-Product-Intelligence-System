package ecommerceai.dto.response;

import ecommerceai.entity.CartItem;

import java.util.List;

//get cart ...response dto
public class CartResponse {
    private Long cartId;
    private Double totalPrice;
    private Integer totalItems;
    private List<CartItem> items;
}
