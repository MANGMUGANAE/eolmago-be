package com.mangmuganae.eolmago.domain.dealpostreaction.repository;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpostreaction.entity.DealPostReaction;
import com.mangmuganae.eolmago.domain.dealpostreaction.entity.ReactionType;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DealPostReactionRepository extends JpaRepository<DealPostReaction, Long> {

  boolean existsByDealPostAndUser(DealPost dealPost, User user);

  @Query("select count(r) from DealPostReaction r "
      + "where r.dealPost = :dealPost and r.reactionType = :reactionType")
  long countByDealPostAndType(DealPost dealPost, ReactionType reactionType);

  Optional<DealPostReaction> findByUserAndDealPost(User user, DealPost dealPost);


}
