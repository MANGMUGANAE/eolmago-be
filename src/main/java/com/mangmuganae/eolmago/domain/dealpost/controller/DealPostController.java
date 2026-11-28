package com.mangmuganae.eolmago.domain.dealpost.controller;

import com.mangmuganae.eolmago.domain.dealpost.docs.DealPostControllerSpecification;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDetailResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostReactionResponse;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchRequestDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostUploadResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostVerificationResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.service.DealPostService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deal-posts")
public class DealPostController implements DealPostControllerSpecification {

  private final DealPostService dealPostService;

  @Override
  @PostMapping("/{dealPostId}/like")
  public ResponseDTO<DealPostReactionResponse> likeDealPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long dealPostId
  ) {
    DealPostReactionResponse dealPostReactionResponse = dealPostService.like(userDetails.getUuid(), dealPostId);

    return ResponseDTO.of(dealPostReactionResponse, "성공적으로 호출되었습니다");
  }


  @Override
  @PostMapping("/{dealPostId}/dislike")
  public ResponseDTO<DealPostReactionResponse> dislikeDealPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long dealPostId
  ) {
    DealPostReactionResponse dealPostReactionResponse = dealPostService.dislike(userDetails.getUuid(), dealPostId);

    return ResponseDTO.of(dealPostReactionResponse, "성공적으로 호출되었습니다");
  }


  @Override
  @GetMapping("/{dealPostId}")
  public ResponseDTO<DealPostDetailResponseDto> getDealPostDetailById(@PathVariable Long dealPostId) {

    DealPostDetailResponseDto dealPostDetailResponseDto = dealPostService.getDealPostDetailById(dealPostId);
    return ResponseDTO.of(dealPostDetailResponseDto, "조회에 성공하였습니다.");
  }

  @Override
  @GetMapping
  public ResponseDTO<DealPostSearchResponseDto> search(
      @ParameterObject DealPostSearchRequestDto dealPostSearchRequestDto) {

    DealPostSearchResponseDto dealPostSearchResponseDto = dealPostService
        .search(dealPostSearchRequestDto);

    return ResponseDTO.of(dealPostSearchResponseDto, "핫딜 목록 조회에 성공하였습니다.");
  }

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseDTO<DealPostUploadResponseDto> upload(
      @RequestPart("request") DealPostDto dealPostDto,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    DealPostUploadResponseDto dealPostUploadResponseDto = dealPostService
        .upload(dealPostDto, files, uuid);

    return ResponseDTO.created(dealPostUploadResponseDto, "핫딜 글 업로드에 성공하였습니다.");
  }

  @Override
  @PostMapping("/{dealPostId}/verifications/link")
  public ResponseDTO<DealPostVerificationResponseDto> verifyLink(
      @PathVariable Long dealPostId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    DealPostVerificationResponseDto dealPostVerificationResponseDto = dealPostService
        .verifyLink(dealPostId, uuid);

    return ResponseDTO.of(dealPostVerificationResponseDto, "링크 검증을 완료하였습니다.");
  }

  @Override
  @PostMapping("/{dealPostId}/verifications/reliability")
  public ResponseDTO<DealPostVerificationResponseDto> verifyReliability(
      @PathVariable Long dealPostId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    DealPostVerificationResponseDto dealPostVerificationResponseDto = dealPostService
        .verifyReliability(dealPostId, uuid);

    return ResponseDTO.of(dealPostVerificationResponseDto, "업로드자 신뢰도 검증을 완료하였습니다.");
  }

  @Override
  @PostMapping("/{dealPostId}/verifications/advertisement")
  public ResponseDTO<DealPostVerificationResponseDto> verifyAdvertisement(
      @PathVariable Long dealPostId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    DealPostVerificationResponseDto dealPostVerificationResponseDto = dealPostService
        .verifyAdvertisement(dealPostId, uuid);

    return ResponseDTO.of(dealPostVerificationResponseDto, "광고글 여부 검증을 완료하였습니다.");
  }



}
