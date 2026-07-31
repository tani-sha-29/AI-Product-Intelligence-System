package ecommerceai.dto.response;

import ecommerceai.entity.CartItem;

import java.util.List;

//get cart ...response dto
public class CartResponse {
    private Long cartId;
    private Double totalPrice;
    private Integer totalItems;
    private List<CartItem> items;

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
    public long getCartId() {
        return cartId;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    public Integer getTotalItems() {
        return totalItems;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    public List<CartItem> getItems() {
        return items;
    }

}
