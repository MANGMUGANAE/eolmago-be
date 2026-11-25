package com.mangmuganae.eolmago.domain.store.entity;

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

  private String store;

  @Builder
  private Store(Long id, String store) {
    this.id = id;
    this.store = store;
  }

  public static Store of(String store) {
    return Store.builder()
        .id(null)
        .store(store)
        .build();
  }

}
