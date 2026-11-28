package com.mangmuganae.eolmago.domain.dealpost.repository;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostStatus;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealPostRepository extends JpaRepository<DealPost, Long> {


  @Query("select d from DealPost d "
      + "where d.user = :user and d.dealPostStatus = :dealPostStatus "
      + "order by d.id desc limit 1")
  Optional<DealPost> findPreviousDealPost(User user, DealPostStatus dealPostStatus);

  @Query("select count(distinct d.purchaseLink) from DealPost d where d.user = :user")
  long countDistinctPurchaseLinkByUser(User user);
  @Query("select count(r) from DealPost r where r.user = :user")
  long countByUser(User user);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update DealPost d set d.likeCount = d.likeCount + 1 where d = :d")
  void increaseLikeCount(@Param("d") DealPost dealPost);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update DealPost d set d.likeCount = d.likeCount - 1 where d = :d")
  void decreaseLikeCount(@Param("d") DealPost dealPost);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update DealPost d set d.dislikeCount = d.dislikeCount + 1 where d = :d")
  void increaseDisLikeCount(@Param("d") DealPost dealPost);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update DealPost d set d.dislikeCount = d.dislikeCount - 1 where d = :d")
  void decreaseDisLikeCount(@Param("d") DealPost dealPost);

  @Modifying(flushAutomatically = true)
  @Query("update DealPost d set d.commentCount = d.commentCount + 1 where d = :d")
  void increaseCommentCount(@Param("d") DealPost dealPost);

  @Modifying(flushAutomatically = true)
  @Query("update DealPost d set d.commentCount = "
      + "case when d.commentCount - :count < 0 then 0 else d.commentCount - :count end "
      + "where d = :d")
  void decreaseCommentCount(@Param("d") DealPost dealPost, @Param("count") int count);


  @Query(value = """
      select dealPost from DealPost dealPost
      left join fetch dealPost.store
      left join fetch dealPost.user uploader
      where (dealPost.deleteYn is null or dealPost.deleteYn = 'N')
        and dealPost.dealPostStatus in :statuses
        and (:categoryId is null or dealPost.category.id = :categoryId)
        and (:keyword is null or dealPost.productName like concat('%', :keyword, '%'))
        and (:hotDealOnly = false
          or (uploader.jjandolRankLevel >= :minRankLevel
            and dealPost.dealReliabilityScore <= :maxReliabilityScore))
      """,
      countQuery = """
      select count(dealPost) from DealPost dealPost
      left join dealPost.user uploader
      where (dealPost.deleteYn is null or dealPost.deleteYn = 'N')
        and dealPost.dealPostStatus in :statuses
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

  long countByUserAndDealPostStatus(User user, DealPostStatus dealPostStatus);
}
