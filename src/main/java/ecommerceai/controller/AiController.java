package ecommerceai.controller;

import ecommerceai.dto.response.ApiResponse;
import ecommerceai.dto.response.RecommendationResponse;
import ecommerceai.service.AiRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiRecommendService aiRecommendService;

    public AiController(AiRecommendService aiRecommendService) {
        this.aiRecommendService = aiRecommendService;
    }

    @GetMapping("/recommend/{productId}")
    @Operation(summary = "Recommend products",
            description = "recommend complementary products"
    )
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @PathVariable Long productId) {

        List<RecommendationResponse> recommendations =
                aiRecommendService.getRecommendations(productId, 5);

        ApiResponse<List<RecommendationResponse>> response =
                new ApiResponse<>(
                        true,
                        "Recommendations fetched successfully",
                        recommendations
                );

        return ResponseEntity.ok(response);
    }
}
