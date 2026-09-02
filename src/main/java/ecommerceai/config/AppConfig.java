package ecommerceai.config;

import ecommerceai.repository.IUserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    private final IUserRepo userRepo;

    public AppConfig(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return name -> userRepo.findByName(name)
                .map(user -> org.springframework.security.core.userdetails.User.withUsername(user.getName())
                        .password(user.getPassword())
                        .roles(user.getRole() != null ? user.getRole() : "USER")
                        .build())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "User not found in database with name: " + name));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


