package com.mangmuganae.eolmago.domain.store.mapping;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * 상품 링크의 호스트로 판매처명을 찾아주는 매핑입니다.
 * store-domain-mapping.json 을 애플리케이션 시작 시 메모리에 올려 사용합니다.
 */
@Component
public class StoreDomainMapping {

  private static final String MAPPING_LOCATION = "store-domain-mapping.json";

  private final Map<String, String> storeNameByHost;

  public StoreDomainMapping(ObjectMapper objectMapper) {
    this.storeNameByHost = load(objectMapper);
  }

  /**
   * 상품 링크에서 호스트를 추출해 판매처명을 찾습니다.
   * 서브도메인이 더 구체적인 매핑(smartstore.naver.com)을 먼저 선택합니다.
   */
  public Optional<String> findStoreName(String url) {

    String host = extractHost(url);

    if (host == null) {
      return Optional.empty();
    }

    return storeNameByHost.entrySet().stream()
        .filter(entry -> host.equals(entry.getKey()) || host.endsWith("." + entry.getKey()))
        .max((left, right) -> Integer.compare(left.getKey().length(), right.getKey().length()))
        .map(Map.Entry::getValue);
  }

  private String extractHost(String url) {

    if (url == null || url.isBlank()) {
      return null;
    }

    try {
      String host = URI.create(url.trim()).getHost();
      return host == null ? null : host.toLowerCase();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private Map<String, String> load(ObjectMapper objectMapper) {

    try (InputStream inputStream = new ClassPathResource(MAPPING_LOCATION).getInputStream()) {

      List<StoreDomain> storeDomains = objectMapper
          .readValue(inputStream, new TypeReference<List<StoreDomain>>() {
          });

      Map<String, String> mapping = new HashMap<>();

      for (StoreDomain storeDomain : storeDomains) {
        for (String host : storeDomain.hosts()) {
          mapping.put(host.toLowerCase(), storeDomain.storeName());
        }
      }

      return Map.copyOf(mapping);

    } catch (Exception e) {
      throw new IllegalStateException("판매처 도메인 매핑 로드 실패: " + MAPPING_LOCATION, e);
    }
  }

  private record StoreDomain(String storeName, List<String> hosts) {

  }

}
