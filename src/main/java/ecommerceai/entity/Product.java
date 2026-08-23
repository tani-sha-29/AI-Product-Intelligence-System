package ecommerceai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    @NotBlank
    @Size(min = 5)
    private String description;

    @NotBlank
    @Size(min = 1)
    private String category;

    @NotBlank
    @Size(min = 1)
    private Integer categoryId;

    @NotBlank
    @Size(min = 1)
    @Email
    private Double price;

    @NotNull
    private String image;

    public Product(){
    }

    public void setId(Long id){
        this.id=id;
    }
    public long getId(){
        return id;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getDescription(){
        return description;
    }
    public void setCategory(String category){
        this.category=category;
    }
    public String getCategory(){
        return category;
    }
    public void setPrice(Double price){
        this.price=price;
    }
    public Double getPrice(){
        return price;
    }
    public void setImage(String image){
        this.image=image;
    }
    public String getImage(){
        return image;
    }


}
