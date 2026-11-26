package com.mangmuganae.eolmago.domain.dealpost.repository;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostStatus;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealPostRepository extends JpaRepository<DealPost, Long> {

  long countByUser(User user);

  @Query(value = """
      select dealPost from DealPost dealPost
      left join fetch dealPost.store
      left join fetch dealPost.user uploader
      where dealPost.dealPostStatus in :statuses
        and (:categoryId is null or dealPost.category.id = :categoryId)
        and (:keyword is null or dealPost.productName like concat('%', :keyword, '%'))
        and (:hotDealOnly = false
          or (uploader.jjandolRankLevel >= :minRankLevel
            and dealPost.dealReliabilityScore <= :maxReliabilityScore))
      """,
      countQuery = """
      select count(dealPost) from DealPost dealPost
      left join dealPost.user uploader
      where dealPost.dealPostStatus in :statuses
        and (:categoryId is null or dealPost.category.id = :categoryId)
        and (:keyword is null or dealPost.productName like concat('%', :keyword, '%'))
        and (:hotDealOnly = false
          or (uploader.jjandolRankLevel >= :minRankLevel
            and dealPost.dealReliabilityScore <= :maxReliabilityScore))
      """)
  Page<DealPost> search(
      @Param("statuses") Collection<DealPostStatus> statuses,
      @Param("categoryId") Long categoryId,
      @Param("keyword") String keyword,
      @Param("hotDealOnly") boolean hotDealOnly,
      @Param("minRankLevel") int minRankLevel,
      @Param("maxReliabilityScore") int maxReliabilityScore,
      Pageable pageable);

}
