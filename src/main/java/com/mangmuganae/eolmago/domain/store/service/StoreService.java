package com.mangmuganae.eolmago.domain.store.service;

import com.mangmuganae.eolmago.domain.store.dto.StoreDto;
import com.mangmuganae.eolmago.domain.store.dto.StoreResponseDto;
import com.mangmuganae.eolmago.domain.store.entity.Store;
import com.mangmuganae.eolmago.domain.store.exception.StoreNotFoundException;
import com.mangmuganae.eolmago.domain.store.mapping.StoreDomainMapping;
import com.mangmuganae.eolmago.domain.store.repository.StoreRepository;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;

  private final StoreDomainMapping storeDomainMapping;

  /**
   * 상품 링크의 호스트로 판매처를 찾습니다. 외부 요청 없이 매핑만으로 판별합니다.
   * 등록되지 않은 판매처라면 STORE_NOT_FOUND 로 404 응답됩니다.
   */
  public StoreDto searchByPurchaseLink(String purchaseLink) {

    return storeDomainMapping.findStoreName(purchaseLink)
        .flatMap(storeRepository::findByStoreName)
        .map(Store::toDto)
        .orElseThrow(() -> new StoreNotFoundException(ErrorCode.STORE_NOT_FOUND));
  }

  /**
   * 판매처명에 검색어가 포함된 판매처를 모두 조회합니다.
   * 링크로 판매처를 찾지 못했을 때 직접 선택할 수 있도록 사용합니다.
   */
  public StoreResponseDto searchByStoreName(String keyword) {

    String searchKeyword = (keyword == null) ? "" : keyword.trim();

    List<Store> stores = storeRepository.findByStoreNameContaining(searchKeyword);

    return StoreResponseDto.of(
        stores.stream()
            .map(Store::toDto)
            .toList()
    );
  }

}
