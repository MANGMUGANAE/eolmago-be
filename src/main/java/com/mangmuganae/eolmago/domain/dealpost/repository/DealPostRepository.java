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

  /**
   * 핫딜 목록을 조건별로 조회합니다.
   * 목록에서 판매처명과 업로드자 등급을 쓰므로 store 와 user 는 함께 조회합니다.
   *
   * @param statuses             노출할 상태입니다. 종료 글 제외 시 ACTIVE 만 넘깁니다.
   * @param categoryId           null 이면 카테고리 조건을 적용하지 않습니다.
   * @param keyword              null 이면 검색어 조건을 적용하지 않습니다.
   * @param hotDealOnly          false 면 아래 두 조건을 적용하지 않습니다.
   * @param minRankLevel         핫딜 필터에서 요구하는 업로드자 최소 등급 레벨입니다.
   * @param maxReliabilityScore  핫딜 필터에서 허용하는 최대 핫딜 신뢰도 점수입니다.
   */
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
