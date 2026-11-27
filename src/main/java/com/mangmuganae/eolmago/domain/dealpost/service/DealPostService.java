package com.mangmuganae.eolmago.domain.dealpost.service;

import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import com.mangmuganae.eolmago.domain.alert.repository.AlertRepository;
import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.category.exception.CategoryNotFoundException;
import com.mangmuganae.eolmago.domain.category.repository.CategoryRepository;
import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.comment.repository.CommentRepository;
import com.mangmuganae.eolmago.domain.dealpost.constant.AffiliateLinkPattern;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostCommentResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDetailResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostPreivewResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchRequestDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostUploadResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostVerificationResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostStatus;
import com.mangmuganae.eolmago.domain.dealpost.exception.AdClassificationFailedException;
import com.mangmuganae.eolmago.domain.dealpost.exception.DealPostAccessDeniedException;
import com.mangmuganae.eolmago.domain.dealpost.exception.DealPostNotFoundException;
import com.mangmuganae.eolmago.domain.dealpost.exception.ImageFileCountExceededException;
import com.mangmuganae.eolmago.domain.dealpost.repository.DealPostRepository;
import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.domain.imagefile.repository.ImageFileRepository;
import com.mangmuganae.eolmago.domain.notification.config.NotificationWaitingManage;
import com.mangmuganae.eolmago.domain.notification.dto.NotificationWaitingObject;
import com.mangmuganae.eolmago.domain.notification.entity.Notification;
import com.mangmuganae.eolmago.domain.notification.entity.NotificationType;
import com.mangmuganae.eolmago.domain.notification.repository.NotificationRepository;
import com.mangmuganae.eolmago.domain.store.entity.Store;
import com.mangmuganae.eolmago.domain.store.exception.PurchaseLinkMissingException;
import com.mangmuganae.eolmago.domain.store.exception.StoreNotFoundException;
import com.mangmuganae.eolmago.domain.store.repository.StoreRepository;
import com.mangmuganae.eolmago.domain.user.entity.JjandolRank;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.config.FileStorage;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import com.mangmuganae.eolmago.infra.impersonator.ImpersonatorUtil;
import com.mangmuganae.eolmago.infra.lamda.LamdaUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealPostService {

  private final AlertRepository alertRepository;
  private final NotificationRepository notificationRepository;
  private final CommentRepository commentRepository;


  private static final int MAX_IMAGE_FILE_COUNT = 10;

  //업로드 글이 이 개수보다 적으면 신뢰도 검증에서 제외합니다.
  private static final long RELIABILITY_EXCLUSION_POST_COUNT = 3;

  //신뢰도가 이 값을 초과하면 통과입니다.
  private static final double RELIABILITY_PASS_SCORE = 0.4;

  //광고일 확률이 이 값 이상이면 광고로 판단합니다.
  private static final double ADVERTISEMENT_THRESHOLD = 0.5;

  //핫딜 필터에서 요구하는 업로드자 최소 등급입니다. (짠돌 중수)
  private static final int HOT_DEAL_MIN_RANK_LEVEL = JjandolRank.INTERMEDIATE.getLevel();

  //핫딜 필터에서 허용하는 최대 핫딜 신뢰도 점수입니다.
  private static final int HOT_DEAL_MAX_RELIABILITY_SCORE = 9;

  //목록에 노출하는 상태입니다. 검증 전(HOLD) 과 거절(REJECTED) 은 조회되지 않습니다.
  private static final List<DealPostStatus> VISIBLE_STATUSES =
      List.of(DealPostStatus.ACTIVE, DealPostStatus.ENDED);

  private static final List<DealPostStatus> ONGOING_STATUSES =
      List.of(DealPostStatus.ACTIVE);

  private final DealPostRepository dealPostRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final StoreRepository storeRepository;
  private final ImageFileRepository imageFileRepository;

  private final FileStorage fileStorage;
  private final ImpersonatorUtil impersonatorUtil;
  private final LamdaUtil lamdaUtil;

  private final NotificationWaitingManage notificationWaitingManage;

  /**
   * 핫딜 목록을 조건별로 조회합니다.
   * 검증을 통과하지 않은 글(HOLD)과 거절된 글(REJECTED)은 조회되지 않습니다.
   */
  @Transactional(readOnly = true)
  public DealPostSearchResponseDto search(DealPostSearchRequestDto dealPostSearchRequestDto) {

    Page<DealPost> dealPostPage = dealPostRepository.search(
        dealPostSearchRequestDto.isEndedExcluded() ? ONGOING_STATUSES : VISIBLE_STATUSES,
        dealPostSearchRequestDto.getCategory(),
        searchKeywordOf(dealPostSearchRequestDto.getKeyword()),
        dealPostSearchRequestDto.isHotDealOnly(),
        HOT_DEAL_MIN_RANK_LEVEL,
        HOT_DEAL_MAX_RELIABILITY_SCORE,
        PageRequest.of(
            dealPostSearchRequestDto.getPage(),
            dealPostSearchRequestDto.getSize(),
            dealPostSearchRequestDto.getSortType().toSort()
        )
    );

    Map<Long, String> thumbnailImageUrls = thumbnailImageUrlsOf(
        dealPostPage.getContent().stream().map(DealPost::getId).toList()
    );

    return DealPostSearchResponseDto.of(
        dealPostPage,
        dealPostPage.getContent().stream()
            .map(dealPost -> DealPostPreivewResponseDto
                .of(dealPost, thumbnailImageUrls.get(dealPost.getId())))
            .toList()
    );
  }

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
    String keyword = dealPostDto.getProductName();

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

    // 알림 처리
    NotificationWaitingObject notificationWaitingObject = NotificationWaitingObject.builder()
            .category(category)
                .keyword(keyword)
                    .dealPost(dealPost)
                        .user(user)
                            .build();

    sendNotification(notificationWaitingObject);

    return DealPostUploadResponseDto.of(dealPost.getId());
  }

  /**
   * 1단계 : 최종 리다이렉트 URL 이 제휴 / 추천인 / 리워드 링크인지 검증합니다.
   */
  @Transactional
  public DealPostVerificationResponseDto verifyLink(Long dealPostId, String uuid) {

    DealPost dealPost = findOwnedDealPost(dealPostId, uuid);
    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    if (dealPost.getPurchaseLink() == null || dealPost.getPurchaseLink().isBlank()) {
      throw new PurchaseLinkMissingException(ErrorCode.PURCHASE_LINK_MISSING);
    }

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

    double adConfidence = adConfidenceOf(dealPost);

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

  /**
   * 공백만 들어온 검색어는 조건에서 제외되도록 null 로 바꿉니다.
   */
  private String searchKeywordOf(String keyword) {
    return (keyword == null || keyword.isBlank()) ? null : keyword.trim();
  }

  /**
   * 조회된 글들의 썸네일을 한 번에 가져와 글 ID 로 찾을 수 있게 만듭니다.
   */
  private Map<Long, String> thumbnailImageUrlsOf(Collection<Long> dealPostIds) {

    if (dealPostIds.isEmpty()) {
      return Map.of();
    }

    return imageFileRepository.findFirstImageFilesByDealPostIds(dealPostIds).stream()
        .collect(Collectors.toMap(
            imageFile -> imageFile.getDealPost().getId(),
            ImageFile::getImageFileUrl
        ));
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

  /**
   * 광고 분류 람다 호출이 실패하면 서버 내부 오류와 구분되도록 AD_CLASSIFICATION_FAILED 로 변환합니다.
   */
  private double adConfidenceOf(DealPost dealPost) {

    try {
      return lamdaUtil.getAdConfidence(advertisementTargetTextOf(dealPost));
    } catch (Exception e) {
      log.error("광고 분류 람다 호출 실패 - dealPostId: {}", dealPost.getId(), e);
      throw new AdClassificationFailedException(ErrorCode.AD_CLASSIFICATION_FAILED);
    }
  }

  private String advertisementTargetTextOf(DealPost dealPost) {

    String productName = dealPost.getProductName() == null ? "" : dealPost.getProductName();
    String content = dealPost.getContent() == null ? "" : dealPost.getContent();

    return (productName + " " + content).trim();
  }

  public void sendNotification(NotificationWaitingObject notificationWaitingObject) {

    this.notificationWaitingManage.add(notificationWaitingObject);

  }

  public DealPostDetailResponseDto getDealPostDetailById(Long dealPostId) {

    DealPost dealPost = dealPostRepository.findById(dealPostId).orElseThrow(
        () -> new DealPostNotFoundException(ErrorCode.POST_NOT_FOUND)
    );

    User user = dealPost.getUser();

    List<Comment> comments = commentRepository.findByDealPost(dealPost);

    return DealPostDetailResponseDto.createByDealPostAndUserAndComments(dealPost, user, comments);

  }
}
