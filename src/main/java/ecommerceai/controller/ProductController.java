package ecommerceai.controller;

import ecommerceai.dto.response.ApiResponse;
import ecommerceai.entity.Product;
import ecommerceai.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page products = productService.getProducts(pageable);

        ApiResponse<Page<Product>> response =
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
    public ResponseEntity<ApiResponse<Page<Product>>> getByCategory(@RequestParam String category,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size
                                                                    ){
        Pageable pageable = PageRequest.of(page, size);
        Page products= productService.getProductByCategory(category,pageable);

        ApiResponse<Page<Product>> response =
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
    public ResponseEntity<ApiResponse<Page<Product>>> getPriceRange(@RequestParam Double minPrice,@RequestParam Double maxPrice,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Page products= productService.findByPriceBetween(minPrice,maxPrice,pageable);
        ApiResponse<Page<Product>> response =
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
    public ResponseEntity<ApiResponse<Page<Product>>> getByKeyword(@RequestParam String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page products= productService.findByDescriptionIs(keyword,pageable);
        ApiResponse<Page<Product>> response =
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
