package ecommerceai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "products")
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, name="product_name",columnDefinition = "TEXT")
    @Size(min = 1)
    private String name;

    @NotBlank
    @Size(min = 5)
    @Column(name = "description" ,columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(min = 1)
    @Column(name = "category",columnDefinition = "TEXT")
    private String category;

    @NotNull
    @PositiveOrZero
    @Column(name="price")
    private Double price;

    @NotBlank
    @Column(nullable = false,name = "image_url",columnDefinition = "TEXT")
    private String image;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", columnDefinition = "vector(512)")
    private float[] embedding;

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
    public void setEmbedding(float[] embedding){
        this.embedding=embedding;
    }
    public float[] getEmbedding(){
        return embedding;
    }


}
