package ecommerceai.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Double itemPrice;


    //getters and setters

    public CartItem() {};

    public CartItem(Integer quantity, Double itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

}