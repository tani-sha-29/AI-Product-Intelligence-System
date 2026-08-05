package ecommerceai.dto.request;

public class CartRequest {
    private Long UserId;
    private Long ProductId;
    private Integer Quantity;


    public void setUserId(Long UserId) {
        this.UserId = UserId;
    }
    public Long getUserId() {
        return UserId;
    }
    public void setProductId(Long ProductId) {
        this.ProductId = ProductId;
    }
    public Long getProductId() {
        return ProductId;
    }
    public void setQuantity(Integer Quantity) {
        this.Quantity = Quantity;
    }
    public Integer getQuantity() {
        return Quantity;
    }


}
