package ecommerceai.controller;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.ApiResponse;
import ecommerceai.dto.response.CartResponse;
import ecommerceai.entity.CartItem;
import ecommerceai.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name="Cart Controller", description = "Endpoints for adding and removing items in cart")
public class CartController {

    CartService cartService;

    CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/CartItems")
    @Operation(
            summary = "Get all the products added in cart",
            description = "Returns all the items"
    )
    public ResponseEntity<ApiResponse<CartResponse>> getAllProducts() {

        CartResponse cartItems = cartService.getCarts();

        ApiResponse<CartResponse> response =
                new ApiResponse<>(
                        true,
                        "Cart listed successfully",
                        cartItems
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/addToCart")
    @Operation(
            summary="Add Element to cart",
            description ="added to cart"
    )
    public ResponseEntity<ApiResponse<String>> addToCart(@RequestBody CartItem cart) {

        String item= cartService.addToCart(cart);

        ApiResponse<String> response =
                new ApiResponse<>(
                       true,
                       "",
                       item
                );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/item")
    @Operation(
            summary="Delete Element from cart",
            description ="remove cart item"
    )
    public ResponseEntity<ApiResponse<String>> removeFromCart(@RequestBody CartRequest cart) {

        String item=cartService.removeFromCart(cart);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "",
                        item
                );
        return ResponseEntity.ok(response);
    }
}
