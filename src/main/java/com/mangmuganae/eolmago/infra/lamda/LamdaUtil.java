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

  // 람다의 confidence는 argmax로 뽑힌 예측 라벨(일반 또는 광고)에 대한 확신도라,
  // is_ad가 false일 때는 "정상일 확률"이다. 항상 "광고일 확률"로 바꿔서 돌려준다.
  public double getAdConfidence(String text) {
    AdClassifyResponse response = restClient.post()
        .header("x-api-key", apiKey)
        .body(Map.of("text", text))
        .retrieve()
        .body(AdClassifyResponse.class);

    return response.isAd() ? response.confidence() : 1 - response.confidence();
  }

  private record AdClassifyResponse(
      String label,
      @JsonProperty("is_ad") boolean isAd,
      double confidence
  ) {}
}
