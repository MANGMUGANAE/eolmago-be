package com.mangmuganae.eolmago.domain.comment.controller;

import com.mangmuganae.eolmago.domain.comment.docs.CommentControllerSpecification;
import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateRequestDto;
import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateResponseDto;
import com.mangmuganae.eolmago.domain.comment.service.CommentService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deal-posts")
public class CommentController implements CommentControllerSpecification {

  private final CommentService commentService;

  @Override
  @PostMapping("/{dealPostId}/comments")
  public ResponseDTO<CommentCreateResponseDto> createComment(
      @PathVariable Long dealPostId,
      @RequestBody CommentCreateRequestDto commentCreateRequestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    CommentCreateResponseDto commentCreateResponseDto = commentService
        .create(dealPostId, commentCreateRequestDto, uuid);

    return ResponseDTO.created(commentCreateResponseDto, "댓글 작성에 성공하였습니다.");
  }

  @Override
  @DeleteMapping("/{dealPostId}/comments/{commentId}")
  public ResponseDTO<?> deleteComment(
      @PathVariable Long dealPostId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    commentService.delete(dealPostId, commentId, uuid);

    return ResponseDTO.of("댓글 삭제에 성공하였습니다.");
  }

}
