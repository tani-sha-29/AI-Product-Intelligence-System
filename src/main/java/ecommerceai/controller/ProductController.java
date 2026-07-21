package ecommerceai.controller;

import ecommerceai.entity.Product;
import ecommerceai.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name="Product Controller", description = "Endpoints for creating and modifying products")
public class ProductController {

    ProductService productService;

    ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping("/allProducts")
    @Operation(summary = "Get all the products",description = "Returns all the products for your search")
    public ResponseEntity<List<Product>> getAllProducts(){
       List products= productService.getProducts();
       return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    @Operation(summary = "Get all the product by ID",description = "Returns The particular product")
    public ResponseEntity<Product> ProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @GetMapping("/getCategory")
    @Operation(summary = "Get the Category of product",description = "Returns the products category")
    public ResponseEntity<List<Product>> getByCategory(@RequestParam String category){
        List<Product> products= productService.getProductByCategory(category);
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @GetMapping("/getCheaper")
    @Operation(summary = "Get the Cheaper products",description = "Returns the cheaper products")
    public ResponseEntity<List<Product>> getCheap(){
        List<Product> products= productService.getCheapestProducts();
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @GetMapping("/priceRange")
    @Operation(summary = "Get the products between price range",description = "Returns in range of your budget")
    public ResponseEntity<List<Product>> getPriceRange(@RequestParam Double minPrice,@RequestParam Double maxPrice){
        List<Product> products= productService.findByPriceBetween(minPrice,maxPrice);
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @GetMapping("/searchKeyword")
    @Operation(summary = "Enter the keyword for your search",description = "Returns based on the keyword")
    public ResponseEntity<List<Product>> getByKeyword(@RequestParam String keyword){
        List<Product> products= productService.findByDescriptionIs(keyword);
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @PostMapping("/addProduct")
    @Operation(summary = "Add the product",description = "Add the Product from User to Database")
    ResponseEntity<String> addProduct(@RequestBody Product product){
        String response = productService.addProduct(product);
        return new ResponseEntity<String>(response,HttpStatus.CREATED);
    }
    @PutMapping("/updateProduct")
    @Operation(summary = "Update the product",description = "Updates the fields")
    ResponseEntity<String> updateProduct(@RequestBody  Product product){
        String response=productService.updateProduct(product);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/deleteProduct/{id}")
    @Operation(summary = "Deletes the product",description = "Delete the product by id")
    ResponseEntity<String> delete(@PathVariable Long id){
        String response=productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
