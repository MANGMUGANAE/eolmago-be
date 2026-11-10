package com.mangmuganae.eolmago.domain.training.service;

import com.mangmuganae.eolmago.domain.training.entity.Criteria;
import com.mangmuganae.eolmago.domain.training.entity.Evaluation;
import com.mangmuganae.eolmago.domain.training.entity.Evaluator;
import com.mangmuganae.eolmago.domain.training.entity.HotdealPost;
import com.mangmuganae.eolmago.domain.training.repository.CriteriaRepository;
import com.mangmuganae.eolmago.domain.training.repository.EvaluationRepository;
import com.mangmuganae.eolmago.domain.training.repository.EvaluatorRepository;
import com.mangmuganae.eolmago.domain.training.repository.HotdealPostRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {

  private static final int MAX_EVALUATION_COUNT = 3;
  private static final int DUPLICATE_SIMILARITY_THRESHOLD = 90;

  private final EvaluatorRepository evaluatorRepository;
  private final HotdealPostRepository hotdealPostRepository;
  private final EvaluationRepository evaluationRepository;
  private final CriteriaRepository criteriaRepository;

  public TrainingService(EvaluatorRepository evaluatorRepository,
      HotdealPostRepository hotdealPostRepository,
      EvaluationRepository evaluationRepository,
      CriteriaRepository criteriaRepository) {
    this.evaluatorRepository = evaluatorRepository;
    this.hotdealPostRepository = hotdealPostRepository;
    this.evaluationRepository = evaluationRepository;
    this.criteriaRepository = criteriaRepository;
  }

  public record PostItem(String title, String content, Integer label, String sourceUrl) {

  }

  // 이름이 없으면 신규 생성, 있으면 pin 검증. pin이 안 맞으면 empty 반환
  public Optional<Evaluator> login(String name, String pin) {
    Optional<Evaluator> existing = evaluatorRepository.findByName(name);
    if (existing.isEmpty()) {
      return Optional.of(evaluatorRepository.save(name, pin));
    }
    Evaluator evaluator = existing.get();
    if (!evaluator.getPin().equals(pin)) {
      return Optional.empty();
    }
    return Optional.of(evaluator);
  }

  public List<Map<String, Object>> uploadPosts(List<PostItem> items, Evaluator uploader) {
    List<Map<String, Object>> results = new ArrayList<>();
    List<String> existingNormalizedTexts = new ArrayList<>();
    for (HotdealPost existing : hotdealPostRepository.findAll()) {
      existingNormalizedTexts.add(normalizedText(existing.getTitle(), existing.getContent()));
    }

    for (PostItem item : items) {
      String title = maskUrls(item.title());
      String content = maskUrls(item.content());

      Map<String, Object> result = new HashMap<>();
      result.put("title", title);

      String normalized = normalizedText(title, content);
      if (isDuplicate(normalized, existingNormalizedTexts)) {
        result.put("success", false);
        result.put("message", "이미 등록된 내용입니다.");
        results.add(result);
        continue;
      }

      String contentHash = hash(title, content);
      String sourceUrl = item.sourceUrl() == null ? null : item.sourceUrl().trim();
      if (sourceUrl != null && sourceUrl.isEmpty()) {
        sourceUrl = null;
      }
      HotdealPost post = hotdealPostRepository.save(title, content, contentHash, sourceUrl);
      existingNormalizedTexts.add(normalized);

      if (item.label() != null) {
        evaluationRepository.save(post.getId(), uploader.getId(), uploader.getName(), item.label());
      }

      result.put("success", true);
      result.put("postId", post.getId());
      results.add(result);
    }

    return results;
  }

  public List<Map<String, Object>> listPosts(String status, String keyword) {
    String safeKeyword = keyword == null ? "" : keyword;
    List<HotdealPost> posts = hotdealPostRepository.findByStatusAndKeyword(status, safeKeyword);

    List<Map<String, Object>> rows = new ArrayList<>();
    for (HotdealPost post : posts) {
      rows.add(toPostSummary(post));
    }
    return rows;
  }

  public Map<String, Object> getPostDetail(Long postId) {
    HotdealPost post = hotdealPostRepository.findById(postId).orElseThrow();
    List<Evaluation> evaluations = evaluationRepository.findByPostId(postId);

    Map<String, Object> detail = toPostSummary(post);
    detail.put("evaluations", evaluations);
    return detail;
  }

  public Map<String, Object> submitEvaluation(Long postId, Evaluator evaluator, int label) {
    int currentCount = evaluationRepository.countByPostId(postId);
    Map<String, Object> result = new HashMap<>();

    if (currentCount >= MAX_EVALUATION_COUNT) {
      result.put("success", false);
      result.put("message", "이미 평가가 3번 모두 완료된 게시글입니다.");
      return result;
    }

    evaluationRepository.save(postId, evaluator.getId(), evaluator.getName(), label);
    int newCount = currentCount + 1;

    if (newCount >= MAX_EVALUATION_COUNT) {
      finalizePost(postId);
    }

    result.put("success", true);
    result.put("post", toPostSummary(hotdealPostRepository.findById(postId).orElseThrow()));
    return result;
  }

  public Map<String, Object> updateEvaluation(Long evaluationId, int label) {
    Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow();
    evaluationRepository.updateLabel(evaluationId, label);

    Long postId = evaluation.getPostId();
    int count = evaluationRepository.countByPostId(postId);
    if (count >= MAX_EVALUATION_COUNT) {
      finalizePost(postId);
    }

    return toPostSummary(hotdealPostRepository.findById(postId).orElseThrow());
  }

  public void deletePost(Long postId) {
    hotdealPostRepository.deleteById(postId);
  }

  public void deleteEvaluation(Long evaluationId) {
    Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow();
    Long postId = evaluation.getPostId();
    evaluationRepository.deleteById(evaluationId);

    int count = evaluationRepository.countByPostId(postId);
    if (count < MAX_EVALUATION_COUNT) {
      hotdealPostRepository.revertToPending(postId);
    }
  }

  public String getCriteria() {
    return criteriaRepository.findFirst().map(Criteria::getContent).orElse("");
  }

  public void updateCriteria(String content) {
    Criteria criteria = criteriaRepository.findFirst().orElseThrow();
    criteriaRepository.updateContent(criteria.getId(), content);
  }

  public String exportCsv() {
    List<HotdealPost> posts = hotdealPostRepository.findLabeledForExport();

    StringBuilder csv = new StringBuilder("text,label\n");
    for (HotdealPost post : posts) {
      String text = (post.getTitle() + " " + post.getContent())
          .replaceAll("[\\r\\n]+", " ")
          .replace("\"", "\"\"");
      csv.append("\"").append(text).append("\",").append(post.getFinalLabel()).append("\n");
    }
    return csv.toString();
  }

  private void finalizePost(Long postId) {
    List<Evaluation> evaluations = evaluationRepository.findByPostId(postId);
    int finalLabel = majority(evaluations);
    hotdealPostRepository.finalizeLabel(postId, finalLabel);
  }

  // 3개 중 2개 이상 같은 라벨이면 그 라벨, 전부 다르면(0,1,2 각 1번) 애매(2)로 확정
  private int majority(List<Evaluation> evaluations) {
    int[] counts = new int[3];
    for (Evaluation evaluation : evaluations) {
      counts[evaluation.getLabel()]++;
    }

    for (int label = 0; label < 3; label++) {
      if (counts[label] >= 2) {
        return label;
      }
    }
    return Evaluation.LABEL_AMBIGUOUS;
  }

  private Map<String, Object> toPostSummary(HotdealPost post) {
    List<Evaluation> evaluations = evaluationRepository.findByPostId(post.getId());
    List<String> evaluatorNames = new ArrayList<>();
    for (Evaluation evaluation : evaluations) {
      evaluatorNames.add(evaluation.getEvaluatorName());
    }

    Map<String, Object> summary = new HashMap<>();
    summary.put("id", post.getId());
    summary.put("title", post.getTitle());
    summary.put("content", post.getContent());
    summary.put("status", post.getStatus());
    summary.put("finalLabel", post.getFinalLabel());
    summary.put("evaluationCount", evaluations.size());
    summary.put("remaining", MAX_EVALUATION_COUNT - evaluations.size());
    summary.put("evaluatorNames", evaluatorNames);
    summary.put("createdAt", post.getCreatedAt());
    summary.put("sourceUrl", post.getSourceUrl());
    return summary;
  }

  private static final Pattern URL_PATTERN =
      Pattern.compile("https?://[A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+");

  private String maskUrls(String text) {
    if (text == null) {
      return null;
    }
    return URL_PATTERN.matcher(text).replaceAll("[URL]");
  }

  private String stripToTextOnly(String text) {
    if (text == null) {
      return "";
    }
    return text.replaceAll("[^\\p{L}\\p{N}]", "");
  }

  private String normalizedText(String title, String content) {
    return stripToTextOnly(title) + "|" + stripToTextOnly(content);
  }

  // rapidfuzz(Python)와 동일한 ratio 알고리즘을 쓰는 JVM 포트. 90 이상이면 같은 글로 취급
  private boolean isDuplicate(String normalized, List<String> existingNormalizedTexts) {
    for (String existing : existingNormalizedTexts) {
      if (FuzzySearch.ratio(normalized, existing) >= DUPLICATE_SIMILARITY_THRESHOLD) {
        return true;
      }
    }
    return false;
  }

  private String hash(String title, String content) {
    String normalized = normalizedText(title, content);
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder();
      for (byte b : bytes) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

}
