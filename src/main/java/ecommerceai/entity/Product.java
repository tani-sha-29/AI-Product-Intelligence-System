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
    public long getid(){
        return id;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getname(){
        return name;
    }
    public void setdescription(String description){
        this.description=description;
    }
    public String getdescription(){
        return description;
    }
    public void setcategory(String category){
        this.category=category;
    }
    public String getcategory(){
        return category;
    }
    public void setprice(Double price){
        this.price=price;
    }
    public Double getprice(){
        return price;
    }
    public void setimage(String image){
        this.image=image;
    }
    public String getimage(){
        return image;
    }


}
