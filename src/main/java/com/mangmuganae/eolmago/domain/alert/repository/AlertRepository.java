package com.mangmuganae.eolmago.domain.alert.repository;

import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertRepository extends JpaRepository<Alert, Long> {

  @Query("select a from Alert a join fetch a.user where a.category is null")
  List<Alert> findAllByCategoryIdIsNull();

  @Query("select a from Alert a join fetch a.user where a.keyword is null")
  List<Alert> findAllByKeywordIsNull();

}
