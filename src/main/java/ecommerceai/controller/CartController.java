package ecommerceai.controller;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.ApiResponse;
import ecommerceai.dto.response.CartResponse;
import ecommerceai.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name="Cart Controller", description = "Endpoints for Cart Functionality")
public class CartController {

    CartService service;
    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/cartItems/{userId}")
    @Operation(
            summary = "Get cart",
            description = "Gives the Cart of the User"
    )
    ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable Long userId){
        CartResponse cart = service.getCart(userId);

        ApiResponse<CartResponse> response =
                new ApiResponse<>(
                        true,
                        "Cart :",
                        cart
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/addToCart")
    @Operation(
            summary = "Add cart",
            description = "Adds product to the Cart"
    )
    ResponseEntity<ApiResponse<String>> addToCart(@RequestBody CartRequest request){
        String status = service.addToCart(request);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Added",
                        status
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove from cart",
            description = "Deletes the item from Cart"
    )
    ResponseEntity<ApiResponse<String>> remove(@RequestBody CartRequest request){
        String status = service.removeFromCart(request);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Item Removed",
                        status
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/Edit_item")
    @Operation(
            summary = "Update item",
            description = "Updates items in cart"
    )
    ResponseEntity<ApiResponse<String>> UpdateItem(@RequestBody CartRequest request){
        String status = service.updateCart(request);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Item Updated",
                        status
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/Clear")
    @Operation(
            summary = "Clear Cart",
            description = "Clear all items from Cart"
    )
    ResponseEntity<ApiResponse<String>> ClearCart(@PathVariable Long userId){
        String status = service.clearCart(userId);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Cart Cleared",
                        status
                );

        return ResponseEntity.ok(response);
    }

}
