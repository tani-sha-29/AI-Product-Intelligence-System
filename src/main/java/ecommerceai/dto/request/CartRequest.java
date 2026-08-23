package ecommerceai.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CartRequest {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    @Min(1)
    private Integer quantity;


    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getProductId() {
        return productId;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getQuantity() {
        return quantity;
    }


}
