package com.mangmuganae.eolmago.domain.comment.service;

import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateRequestDto;
import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateResponseDto;
import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.comment.exception.CommentAccessDeniedException;
import com.mangmuganae.eolmago.domain.comment.exception.CommentContentRequiredException;
import com.mangmuganae.eolmago.domain.comment.exception.CommentNotFoundException;
import com.mangmuganae.eolmago.domain.comment.exception.CommentReplyDepthExceededException;
import com.mangmuganae.eolmago.domain.comment.repository.CommentRepository;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.exception.DealPostNotFoundException;
import com.mangmuganae.eolmago.domain.dealpost.repository.DealPostRepository;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final DealPostRepository dealPostRepository;
  private final UserRepository userRepository;

  @Transactional
  public CommentCreateResponseDto create(Long dealPostId,
      CommentCreateRequestDto commentCreateRequestDto, String uuid) {

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    DealPost dealPost = dealPostRepository.findById(dealPostId).orElseThrow(
        () -> new DealPostNotFoundException(ErrorCode.POST_NOT_FOUND)
    );

    validateContent(commentCreateRequestDto.getContent());

    Comment parentComment = findParentComment(
        commentCreateRequestDto.getParentCommentId(), dealPost);

    Comment comment = commentRepository.save(
        Comment.of(
            commentCreateRequestDto.getContent().trim(),
            false,
            null,
            user,
            dealPost,
            parentComment
        )
    );

    dealPostRepository.increaseCommentCount(dealPost);

    return CommentCreateResponseDto.of(
        comment.getId(),
        parentComment == null ? null : parentComment.getId()
    );
  }

  @Transactional
  public void delete(Long dealPostId, Long commentId, String uuid) {

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    DealPost dealPost = dealPostRepository.findById(dealPostId).orElseThrow(
        () -> new DealPostNotFoundException(ErrorCode.POST_NOT_FOUND)
    );

    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND)
    );

    if (!comment.getDealPost().getId().equals(dealPost.getId())) {
      throw new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (!comment.getUser().getId().equals(user.getId())) {
      throw new CommentAccessDeniedException(ErrorCode.ACCESS_DENIED);
    }

    List<Comment> replies = commentRepository.findByParentComment(comment);

    commentRepository.deleteAll(replies);
    commentRepository.delete(comment);

    dealPostRepository.decreaseCommentCount(dealPost, replies.size() + 1);
  }

  private void validateContent(String content) {

    if (content == null || content.isBlank()) {
      throw new CommentContentRequiredException(ErrorCode.COMMENT_CONTENT_REQUIRED);
    }
  }

  private Comment findParentComment(Long parentCommentId, DealPost dealPost) {

    if (parentCommentId == null) {
      return null;
    }

    Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(
        () -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND)
    );

    if (!parentComment.getDealPost().getId().equals(dealPost.getId())) {
      throw new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (parentComment.getComment() != null) {
      throw new CommentReplyDepthExceededException(ErrorCode.COMMENT_REPLY_DEPTH_EXCEEDED);
    }

    return parentComment;
  }

}
