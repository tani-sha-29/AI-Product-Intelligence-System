package ecommerceai.controller;

import ecommerceai.dto.request.LoginRequest;
import ecommerceai.dto.request.RegisterUserRequest;
import ecommerceai.dto.response.ApiResponse;
import ecommerceai.dto.response.UserResponse;
import ecommerceai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Login")
@Tag(name="Authentication Controller", description = "Endpoints for user register and login")
public class AuthController {

    UserService userService;

    AuthController(UserService userService){
            this.userService=userService;
        }

        @PostMapping("/Register")
        @Operation(
                summary = "Register a new user",
                description = "Takes User data to Register"
        )
        public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterUserRequest user) {

            UserResponse userResponse= userService.addUser(user);

            ApiResponse<UserResponse> response =
                    new ApiResponse<>(
                            true,
                            "User Registered",
                            userResponse
                    );

            return ResponseEntity.ok(response);
        }

    @PatchMapping("/Login")
    @Operation(
            summary = "Login a user",
            description = "Login the already registered user"
    )
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest user) {

        UserResponse userResponse= userService.login(user);

        ApiResponse<UserResponse> response =
                new ApiResponse<>(
                        true,
                        "User Login Successfully",
                        userResponse
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/Logout")
    @Operation(
            summary = "Log out a user",
            description = "Log out"
    )
    public ResponseEntity<ApiResponse<String>> logout(@PathVariable Long id) {

        String us=userService.logout(id);

        ApiResponse<String> response =
                new ApiResponse<String>(
                        true,
                        "User Logged out",
                        us
                );

        return ResponseEntity.ok(response);
    }

}
