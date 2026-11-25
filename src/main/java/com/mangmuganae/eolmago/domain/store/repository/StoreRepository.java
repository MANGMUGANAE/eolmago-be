package com.mangmuganae.eolmago.domain.store.repository;

import com.mangmuganae.eolmago.domain.store.entity.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

  Optional<Store> findByStoreName(String storeName);

  List<Store> findByStoreNameContaining(String storeName);

}
