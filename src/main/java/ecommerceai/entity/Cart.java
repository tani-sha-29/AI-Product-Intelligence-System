package ecommerceai.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalPrice;

    private Integer totalItems;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Cart() {}

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Integer getTotalItems() {
        return totalItems;
    }
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}

