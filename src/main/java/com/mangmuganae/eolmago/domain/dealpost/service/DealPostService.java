package com.mangmuganae.eolmago.domain.dealpost.service;

import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.category.exception.CategoryNotFoundException;
import com.mangmuganae.eolmago.domain.category.repository.CategoryRepository;
import com.mangmuganae.eolmago.domain.dealpost.constant.AffiliateLinkPattern;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostUploadResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostVerificationResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.exception.DealPostAccessDeniedException;
import com.mangmuganae.eolmago.domain.dealpost.exception.DealPostNotFoundException;
import com.mangmuganae.eolmago.domain.dealpost.exception.ImageFileCountExceededException;
import com.mangmuganae.eolmago.domain.dealpost.repository.DealPostRepository;
import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.domain.imagefile.repository.ImageFileRepository;
import com.mangmuganae.eolmago.domain.store.entity.Store;
import com.mangmuganae.eolmago.domain.store.exception.StoreNotFoundException;
import com.mangmuganae.eolmago.domain.store.repository.StoreRepository;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.config.FileStorage;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import com.mangmuganae.eolmago.infra.impersonator.ImpersonatorUtil;
import com.mangmuganae.eolmago.infra.lamda.LamdaUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DealPostService {

  private static final int MAX_IMAGE_FILE_COUNT = 10;

  //업로드 글이 이 개수보다 적으면 신뢰도 검증에서 제외합니다.
  private static final long RELIABILITY_EXCLUSION_POST_COUNT = 3;

  //신뢰도가 이 값을 초과하면 통과입니다.
  private static final double RELIABILITY_PASS_SCORE = 0.4;

  //광고일 확률이 이 값 이상이면 광고로 판단합니다.
  private static final double ADVERTISEMENT_THRESHOLD = 0.5;

  private final DealPostRepository dealPostRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final StoreRepository storeRepository;
  private final ImageFileRepository imageFileRepository;

  private final FileStorage fileStorage;
  private final ImpersonatorUtil impersonatorUtil;
  private final LamdaUtil lamdaUtil;

  /**
   * 핫딜 글을 업로드합니다. 업로드된 글은 검증 전이므로 HOLD 상태로 저장됩니다.
   */
  @Transactional
  public DealPostUploadResponseDto upload(DealPostDto dealPostDto, List<MultipartFile> files,
      String uuid) {

    User user = findUserByUuid(uuid);

    Category category = categoryRepository.findById(dealPostDto.getCategoryId()).orElseThrow(
        () -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND)
    );

    Store store = findStore(dealPostDto.getStoreId());

    validateImageFileCount(files);

    DealPost dealPost = dealPostRepository.save(
        DealPost.createHold(
            dealPostDto.getPurchaseLink(),
            dealPostDto.getProductName(),
            dealPostDto.getProductPrice(),
            dealPostDto.getContent(),
            user,
            category,
            store
        )
    );

    saveImageFiles(files, dealPost);

    return DealPostUploadResponseDto.of(dealPost.getId());
  }

  /**
   * 1단계 : 최종 리다이렉트 URL 이 제휴 / 추천인 / 리워드 링크인지 검증합니다.
   */
  @Transactional
  public DealPostVerificationResponseDto verifyLink(Long dealPostId, String uuid) {

    DealPost dealPost = findOwnedDealPost(dealPostId, uuid);

    String finalUrl = impersonatorUtil.resolveFinalUrl(dealPost.getPurchaseLink());
    String targetUrl = (finalUrl == null) ? dealPost.getPurchaseLink() : finalUrl;

    boolean passed = !AffiliateLinkPattern.isAffiliateLink(targetUrl);

    if (passed) {
      dealPost.passLinkVerification();
    }

    return DealPostVerificationResponseDto
        .of(dealPost.getId(), passed, dealPost.getDealPostStatus());
  }

  /**
   * 2단계 : 업로드자의 신뢰도를 검증합니다.
   * 업로드 글이 3개 미만이면 검증에서 제외되어 통과 처리됩니다.
   */
  @Transactional
  public DealPostVerificationResponseDto verifyReliability(Long dealPostId, String uuid) {

    DealPost dealPost = findOwnedDealPost(dealPostId, uuid);

    User uploader = dealPost.getUser();
    long uploadedPostCount = dealPostRepository.countByUser(uploader);

    boolean passed = uploadedPostCount < RELIABILITY_EXCLUSION_POST_COUNT
        || reliabilityScoreOf(uploader) > RELIABILITY_PASS_SCORE;

    if (passed) {
      dealPost.passReliabilityVerification();
    }

    return DealPostVerificationResponseDto
        .of(dealPost.getId(), passed, dealPost.getDealPostStatus());
  }

  /**
   * 3단계 : 상품명과 본문을 합친 텍스트로 광고글 여부를 검증합니다.
   */
  @Transactional
  public DealPostVerificationResponseDto verifyAdvertisement(Long dealPostId, String uuid) {

    DealPost dealPost = findOwnedDealPost(dealPostId, uuid);

    double adConfidence = lamdaUtil.getAdConfidence(advertisementTargetTextOf(dealPost));

    boolean passed = adConfidence < ADVERTISEMENT_THRESHOLD;

    if (passed) {
      dealPost.passAdvertisementVerification();
    }

    return DealPostVerificationResponseDto
        .of(dealPost.getId(), passed, dealPost.getDealPostStatus());
  }

  private DealPost findOwnedDealPost(Long dealPostId, String uuid) {

    User user = findUserByUuid(uuid);

    DealPost dealPost = dealPostRepository.findById(dealPostId).orElseThrow(
        () -> new DealPostNotFoundException(ErrorCode.POST_NOT_FOUND)
    );

    if (!dealPost.isOwnedBy(user)) {
      throw new DealPostAccessDeniedException(ErrorCode.ACCESS_DENIED);
    }

    return dealPost;
  }

  private User findUserByUuid(String uuid) {

    return userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );
  }

  private Store findStore(Long storeId) {

    if (storeId == null) {
      return null;
    }

    return storeRepository.findById(storeId).orElseThrow(
        () -> new StoreNotFoundException(ErrorCode.STORE_NOT_FOUND)
    );
  }

  private void validateImageFileCount(List<MultipartFile> files) {

    if (files != null && files.size() > MAX_IMAGE_FILE_COUNT) {
      throw new ImageFileCountExceededException(ErrorCode.IMAGE_FILE_COUNT_EXCEEDED);
    }
  }

  private void saveImageFiles(List<MultipartFile> files, DealPost dealPost) {

    if (files == null || files.isEmpty()) {
      return;
    }

    files.stream()
        .filter(file -> !file.isEmpty())
        .forEach(file -> imageFileRepository
            .save(ImageFile.of(fileStorage.store(file), dealPost)));
  }

  private double reliabilityScoreOf(User user) {

    return user.getUserReliabillityScore() == null ? 0.0 : user.getUserReliabillityScore();
  }

  private String advertisementTargetTextOf(DealPost dealPost) {

    String productName = dealPost.getProductName() == null ? "" : dealPost.getProductName();
    String content = dealPost.getContent() == null ? "" : dealPost.getContent();

    return (productName + " " + content).trim();
  }

}
