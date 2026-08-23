package ecommerceai.advice;

import ecommerceai.dto.response.ApiResponse;
import ecommerceai.exception.InvalidRequest;
import ecommerceai.exception.ProductNotFoundException;
import ecommerceai.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>>handleUserNotFound(UserNotFoundException e){
        ApiResponse<String> response=new ApiResponse<>(
                false,
                e.getMessage(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<String>>handleProductNotFound(ProductNotFoundException e){
        ApiResponse<String> response=new ApiResponse<>(
                false,
                e.getMessage(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(InvalidRequest.class)
    public ResponseEntity<ApiResponse<String>>invalidRequest(InvalidRequest e){
        ApiResponse<String> response=new ApiResponse<>(
                false,
                e.getMessage(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>>handleException(Exception e){
        ApiResponse<String> response=new ApiResponse<>(
                false,
                e.getMessage(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
