package ecommerceai.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double price;
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
