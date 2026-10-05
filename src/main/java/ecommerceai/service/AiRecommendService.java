package ecommerceai.service;

import ecommerceai.dto.response.RecommendationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class AiRecommendService {
    private final RestClient aiRestClient;

    public AiRecommendService(RestClient aiRestClient) {
        this.aiRestClient = aiRestClient;
    }

    public List<RecommendationResponse> getRecommendations(Long productId, int topK) {

        RecommendationResponse[] response = aiRestClient.get()
                .uri("/recommend/{productId}?top_k={topK}", productId, topK)
                .retrieve()
                .body(RecommendationResponse[].class);

        return response != null
                ? Arrays.asList(response)
                : List.of();
    }
}
