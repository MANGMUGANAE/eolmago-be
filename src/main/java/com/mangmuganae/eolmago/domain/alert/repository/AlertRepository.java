package com.mangmuganae.eolmago.domain.alert.repository;

import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<Alert, Long> {

  @Query("select a from Alert a join fetch a.user where a.category is null")
  List<Alert> findAllByCategoryIdIsNull();

  @Query("select a from Alert a join fetch a.user where a.keyword is null")
  List<Alert> findAllByKeywordIsNull();

  @Query("select a from Alert a where a = :alert and a.user = :user")
  Optional<Alert> findByAlertAndUser(Alert alert, User user);

  @Query("select a from Alert a left join fetch a.category where a.user = :user order by a.id desc")
  List<Alert> findAllByUser(@Param("user") User user);

  boolean existsByUserAndCategory(User user, Category category);

  boolean existsByUserAndKeyword(User user, String keyword);


}
