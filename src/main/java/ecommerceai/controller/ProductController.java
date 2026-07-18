package ecommerceai.controller;

import ecommerceai.entity.Product;
import ecommerceai.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    ProductService productService;

    ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping("/allProducts")
    public ResponseEntity<List<Product>> getAllProducts(){
       List products= productService.getProducts();

       return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @PostMapping("/addProduct")
    ResponseEntity<String> addProduct(@RequestBody Product product){
        String response = productService.addProduct(product);
        return new ResponseEntity<String>(response,HttpStatus.CREATED);
    }
    @PutMapping("/updateProduct")
    ResponseEntity<String> updateProduct(@RequestBody  Product product){
        String response=productService.updateProduct(product);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/deleteProduct/{id}")
    ResponseEntity<String> delete(@PathVariable Long id){
        String response=productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
