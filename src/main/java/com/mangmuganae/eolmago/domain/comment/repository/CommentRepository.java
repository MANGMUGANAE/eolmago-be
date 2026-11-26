package com.mangmuganae.eolmago.domain.comment.repository;

import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
