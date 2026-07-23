package ecommerceai.service;

import ecommerceai.dto.request.LoginRequest;
import ecommerceai.dto.request.RegisterUserRequest;
import ecommerceai.dto.response.UserResponse;
import ecommerceai.entity.User;

public interface IUserService {
    UserResponse addUser(RegisterUserRequest user);
    UserResponse login(LoginRequest login);
    String logout(User user);
}
