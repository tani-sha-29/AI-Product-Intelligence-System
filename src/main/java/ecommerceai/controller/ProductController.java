package ecommerceai.controller;

import ecommerceai.dto.response.ApiResponse;
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

    @GetMapping("/Get-Product")
    @Operation(
            summary = "Get all the products",
            description = "Returns all the products for your search"
    )
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {

        List<Product> products = productService.getProducts();

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products listed successfully",
                        products
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{id}")
    @Operation( summary = "Get all the product by ID",
            description = "Returns The particular product"
    )
    public ResponseEntity<ApiResponse<Product>> ProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        System.out.println("Controller ID = " + id);
        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product Added Successfully",
                        product
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/ProductsByCategory")
    @Operation(summary = "Get the Category of product",
            description = "Returns the products category"
    )
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@RequestParam String category){
        List<Product> products= productService.getProductByCategory(category);

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products listed successfully",
                        products
                );

        return ResponseEntity.ok(response);
     }


    @GetMapping("/getCheaper")
    @Operation(summary = "Get the Cheaper products",
            description = "Returns the cheaper products"
    )
    public ResponseEntity<ApiResponse<List<Product>>> getCheap(){
        List<Product> products= productService.getCheapestProducts();
        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products listed successfully",
                        products
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/priceRange")
    @Operation(summary = "Get the products between price range",
            description = "Returns in range of your budget"
    )
    public ResponseEntity<ApiResponse<List<Product>>> getPriceRange(@RequestParam Double minPrice,@RequestParam Double maxPrice){
        List<Product> products= productService.findByPriceBetween(minPrice,maxPrice);
        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products listed successfully",
                        products
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/searchKeyword")
    @Operation(summary = "Enter the keyword for your search",
            description = "Returns based on the keyword"
    )
    public ResponseEntity<ApiResponse<List<Product>>> getByKeyword(@RequestParam String keyword){
        List<Product> products= productService.findByDescriptionIs(keyword);
        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products listed successfully",
                        products
                );

        return ResponseEntity.ok(response);

    }

    @PostMapping("/addProduct")
    @Operation(summary = "Add the product",
            description = "Add the Product from User to Database"
    )
    public ResponseEntity<ApiResponse<String>> addProduct(@RequestBody Product product) {
        String message = productService.addProduct(product);

        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Product added successfully",
                message
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateProduct")
    @Operation(summary = "Update the product",
            description = "Updates the fields"
    )
    ResponseEntity<ApiResponse<String>> updateProduct(@RequestBody  Product product){
        String message=productService.updateProduct(product);
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Product added successfully",
                 message
        );

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/deleteProduct/{id}")
    @Operation(summary = "Deletes the product",
            description = "Delete the product by id"
    )
    ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id){
        String message=productService.deleteProduct(id);
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Product added successfully",
                message
        );
        return ResponseEntity.ok(response);
    }

}
