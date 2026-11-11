package com.mangmuganae.eolmago.infra.lamda;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LamdaUtil {

  private final RestClient restClient;
  private final String apiKey;

  public LamdaUtil(@Value("${lambda.ad-classifier.url}") String url,
                    @Value("${lambda.ad-classifier.api-key}") String apiKey) {
    this.restClient = RestClient.create(url);
    this.apiKey = apiKey;
  }

  public double getAdConfidence(String text) {
    AdClassifyResponse response = restClient.post()
        .header("x-api-key", apiKey)
        .body(Map.of("text", text))
        .retrieve()
        .body(AdClassifyResponse.class);

    return response.confidence();
  }

  private record AdClassifyResponse(
      String label,
      @JsonProperty("is_ad") boolean isAd,
      double confidence
  ) {}
}
