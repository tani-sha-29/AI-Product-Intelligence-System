package ecommerceai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Min(8)
    private String password;



    public LoginRequest() {
    }

    public LoginRequest(String name,String email, String password) {
        this.email = email;
        this.password = password;
        this.name = name;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String username) {
        this.name = name;
    }


}
