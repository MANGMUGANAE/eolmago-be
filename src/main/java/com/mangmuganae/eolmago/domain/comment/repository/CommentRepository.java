package com.mangmuganae.eolmago.domain.comment.repository;

import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c from Comment c join fetch c.user where c.dealPost = :dealPost")
  List<Comment> findByDealPost(DealPost dealPost);

  @Query("select count(c) from Comment c where c.user = :user")
  long countByUser(User user);

  @Query("select c from Comment c where c.comment = :parentComment")
  List<Comment> findByParentComment(@Param("parentComment") Comment parentComment);

}
