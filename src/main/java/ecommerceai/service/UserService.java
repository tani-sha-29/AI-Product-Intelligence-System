package ecommerceai.service;

import ecommerceai.dto.request.LoginRequest;
import ecommerceai.dto.request.RegisterUserRequest;
import ecommerceai.dto.response.UserResponse;
import ecommerceai.entity.User;
import ecommerceai.repository.IUserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService implements IUserService {

    IUserRepo userRepo;

    public UserService(IUserRepo userRepo){
        this.userRepo=userRepo;
    }

    @Override
    public UserResponse addUser(RegisterUserRequest request) {

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPassword(request.getPassword());

        user.setRole("CUSTOMER");

        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepo.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    public UserResponse login(LoginRequest login) {
        User user=userRepo.findByEmail(login.getEmail()).orElseThrow(() ->
                new RuntimeException("User does not exist"));

        if (!user.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    @Override
    public String logout(User user) {
        return "Logout Successful";
    }
}
