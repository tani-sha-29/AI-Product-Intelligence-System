package ecommerceai.service;

import ecommerceai.dto.request.CartRequest;
import ecommerceai.dto.response.CartResponse;
import ecommerceai.entity.Cart;
import ecommerceai.entity.CartItem;
import ecommerceai.entity.Product;
import ecommerceai.entity.User;
import ecommerceai.exception.ProductNotFoundException;
import ecommerceai.exception.UserNotFoundException;
import ecommerceai.repository.ICartItemRepo;
import ecommerceai.repository.ICartRepo;
import ecommerceai.repository.IProductRepo;
import ecommerceai.repository.IUserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartService implements ICartService{

    private ICartRepo cartRepo;
    private ICartItemRepo cartItemRepo;
    private IProductRepo productRepo;
    private IUserRepo userRepo;

    public CartService(ICartRepo cartRepo,ICartItemRepo cartItemRepo, IProductRepo productRepo, IUserRepo userRepo) {
        this.cartRepo=cartRepo;
        this.productRepo=productRepo;
        this.userRepo=userRepo;
        this.cartItemRepo=cartItemRepo;
    }

    @Override
    public CartResponse getCart(Long UserId) {
        User user=userRepo.findById(UserId).orElseThrow(()->new UserNotFoundException(
                "User Not Found"
        ));
        Cart cart =cartRepo.findByUserId(UserId).orElseThrow(()->new RuntimeException(
                "Cart Not Found"
        ));

        CartResponse response = new CartResponse();

        response.setCartId(cart.getId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalPrice(cart.getTotalPrice());
        response.setItems(cart.getItems());

        return response;
    }

    @Override
    public String addToCart(CartRequest request) {
        User user= userRepo.findById(request.getUserId()).orElseThrow(()->new UserNotFoundException(
                "User Not Registered"
        ));

        Product product=productRepo.findById(request.getProductId()).orElseThrow(()-> new ProductNotFoundException(
                "Product not exist"
        ));


        Cart cart =cartRepo.findByUserId(request.getUserId()).orElseGet(()->{
            Cart newcart=new Cart();
            newcart.setUser(user);
            newcart.setTotalItems(0);
            newcart.setTotalPrice(0.0);
            newcart.setCreatedAt(LocalDateTime.now());

            return cartRepo.save(newcart);
        });


        CartItem item= cartItemRepo.findByCartIdAndProductId(cart.getId(),product.getId()).orElse(null);

        if(item!=null){
            item.setQuantity(
                    item.getQuantity() + request.getQuantity()
            );

            cartItemRepo.save(item);

        }
        else{
            item = new CartItem();

            item.setCart(cart);
            item.setProduct(product);
            item.setItemPrice(product.getPrice());
            item.setQuantity(request.getQuantity());

            cartItemRepo.save(item);
        }

        updateCartTotals(cart);

        return "Added to Cart";
    }
    private void updateCartTotals(Cart cart) {

        var items = cartItemRepo.findByCart(cart);

        int totalItems = 0;
        double totalPrice = 0.0;

        for (CartItem item : items) {

            totalItems += item.getQuantity();

            totalPrice +=
                    item.getItemPrice() * item.getQuantity();
        }

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);

        cartRepo.save(cart);
    }

    @Override
    public String removeFromCart(CartRequest request) {
        User user=userRepo.findById(request.getUserId()).orElseThrow(()->new RuntimeException(
                "User Not Registered"
        ));
        Cart cart=cartRepo.findByUserId(request.getUserId()).orElseThrow(()->new RuntimeException(
                "Cart Not Found"
        ));

        Product product=cartItemRepo.findByProductId(request.getProductId()).orElseThrow(()->new RuntimeException(
                "No Product Found"
        ));

        CartItem item=cartItemRepo.findByCartIdAndProductId(cart.getId(),product.getId()).orElseThrow(()->new RuntimeException(
                "Item Not Found"
        ));
        cartItemRepo.delete(item);
        updateCartTotals(cart);
        return "Product Removed";
    }

    @Override
    public String updateCart(CartRequest request) {
        User user=userRepo.findById(request.getUserId()).orElseThrow(()->new RuntimeException(
                "User Not Registered"
        ));
        Cart cart=cartRepo.findByUserId(request.getUserId()).orElseThrow(()->new RuntimeException(
                "Cart Not Found"
        ));

        Product product=cartItemRepo.findByProductId(request.getProductId()).orElseThrow(()->new RuntimeException(
                "No Product Found"
        ));

        CartItem item=cartItemRepo.findByCartIdAndProductId(cart.getId(),product.getId()).orElseThrow(()->new RuntimeException(
                "Item Not Found"
        ));
        item.setQuantity(request.getQuantity()+item.getQuantity());
        cartItemRepo.save(item);
        updateCartTotals(cart);
        return "Cart Updated";
    }

    @Override
    public String clearCart(Long UserId) {
        User user=userRepo.findById(UserId).orElseThrow(()->new RuntimeException(
                "User Not Registered"
        ));
        Cart cart=cartRepo.findByUserId(UserId).orElseThrow(()->new RuntimeException(
                "Cart Not Found"
        ));
        cartRepo.delete(cart);
        updateCartTotals(cart);
        return "";
    }
}
