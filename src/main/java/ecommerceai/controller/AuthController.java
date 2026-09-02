//package ecommerceai.controller;
//
//import ecommerceai.dto.request.LoginRequest;
//import ecommerceai.dto.request.RegisterUserRequest;
//import ecommerceai.dto.response.ApiResponse;
//import ecommerceai.dto.response.UserResponse;
//import ecommerceai.service.UserService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/Login")
//@Tag(name="Authentication Controller", description = "Endpoints for user register and login")
//public class AuthController {
//
//    UserService userService;
//
//    AuthController(UserService userService){
//            this.userService=userService;
//        }
//
//        @PostMapping("/Register")
//        @Operation(
//                summary = "Register a new user",
//                description = "Takes User data to Register"
//        )
//        public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterUserRequest user) {
//
//            UserResponse userResponse= userService.addUser(user);
//
//            ApiResponse<UserResponse> response =
//                    new ApiResponse<>(
//                            true,
//                            "User Registered",
//                            userResponse
//                    );
//
//            return ResponseEntity.ok(response);
//        }
//
//    @PatchMapping("/Login")
//    @Operation(
//            summary = "Login a user",
//            description = "Login the already registered user"
//    )
//    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest user) {
//
//        UserResponse userResponse= userService.login(user);
//
//        ApiResponse<UserResponse> response =
//                new ApiResponse<>(
//                        true,
//                        "User Login Successfully",
//                        userResponse
//                );
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/Logout")
//    @Operation(
//            summary = "Log out a user",
//            description = "Log out"
//    )
//    public ResponseEntity<ApiResponse<String>> logout(@PathVariable Long id) {
//
//        String us=userService.logout(id);
//
//        ApiResponse<String> response =
//                new ApiResponse<String>(
//                        true,
//                        "User Logged out",
//                        us
//                );
//
//        return ResponseEntity.ok(response);
//    }
//
//}
package ecommerceai.controller;

import ecommerceai.config.JwtService;
import ecommerceai.dto.request.LoginRequest;
import ecommerceai.dto.request.RegisterUserRequest;
import ecommerceai.dto.response.ApiResponse;
import ecommerceai.dto.response.UserResponse;
import ecommerceai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Login") // Matches your custom URL prefix path
@Tag(name = "Authentication Controller", description = "Endpoints for user register and login")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Inject application's security beans alongside your UserService
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/Register")
    @Operation(
            summary = "Register a new user",
            description = "Takes User data to Register"
    )
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterUserRequest user) {
        UserResponse userResponse = userService.addUser(user);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User Registered",
                userResponse
        );

        return ResponseEntity.ok(response);
    }

    // Fixed: Changed from @PatchMapping to @PostMapping for standard security compliance
    @PostMapping("/Login")
    @Operation(
            summary = "Login a user",
            description = "Login the already registered user and generate a JWT token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged in and generated authentication token"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Invalid username or password credentials",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest user) {

        // 1. Authenticate credentials against your core user database records
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
        );

        // 2. Process your native application logic down inside your service layer
        UserResponse userResponse = userService.login(user);

        // 3. Extract user metadata details to generate the unique JWT cryptograph
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getName());
        String token = jwtService.generateToken(userDetails.getUsername());

        // 4. Inject the JWT token into your UserResponse model object before dispatching
        // Note: Make sure you add a `token` String field with its getter/setter inside your UserResponse DTO class!
        userResponse.setToken(token);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User Login Successfully",
                userResponse
        );

        return ResponseEntity.ok(response);
    }

    // Fixed: Appended path variable coordinate suffix mapping string matching the implementation signature
    @PostMapping("/Logout/{id}")
    @Operation(
            summary = "Log out a user",
            description = "Log out a user session securely by their identifier key"
    )
    public ResponseEntity<ApiResponse<String>> logout(@PathVariable Long id) {
        String us = userService.logout(id);

        ApiResponse<String> response = new ApiResponse<>(
                true,
                "User Logged out",
                us
        );

        return ResponseEntity.ok(response);
    }
}
