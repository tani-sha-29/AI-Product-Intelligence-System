package ecommerceai;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Product API", version = "1.0", description = "API for managing products of ecom platform")
        ,servers = @Server(
        url = "http://localhost:8080",
        description = "This app is deployed on Local server"
))
public class ECommerceAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceAiApplication.class, args);
    }

}
