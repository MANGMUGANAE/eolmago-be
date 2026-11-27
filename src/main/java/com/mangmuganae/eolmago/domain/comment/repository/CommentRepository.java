package com.mangmuganae.eolmago.domain.comment.repository;

import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c from Comment c join fetch c.user where c.dealPost = :dealPost")
  List<Comment> findByDealPost(DealPost dealPost);

}
