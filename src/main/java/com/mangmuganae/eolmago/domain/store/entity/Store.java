package com.mangmuganae.eolmago.domain.store.entity;

import com.mangmuganae.eolmago.domain.store.dto.StoreDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String storeName;

  @Builder
  private Store(Long id, String storeName) {
    this.id = id;
    this.storeName = storeName;
  }

  public StoreDto toDto() {
    return StoreDto.builder()
        .storeId(this.id)
        .storeName(this.storeName)
        .build();
  }

  public static Store of(String storeName) {
    return Store.builder()
        .id(null)
        .storeName(storeName)
        .build();
  }

}
