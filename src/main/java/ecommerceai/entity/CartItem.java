package ecommerceai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double itemPrice;

    // Many CartItems can belong to one Cart
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // Many CartItems can refer to one Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    public CartItem() {
    }


    // Getters

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public Cart getCart() {
        return cart;
    }

    public Product getProduct() {
        return product;
    }


    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
