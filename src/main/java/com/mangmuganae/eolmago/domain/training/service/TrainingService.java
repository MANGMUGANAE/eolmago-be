package com.mangmuganae.eolmago.domain.training.service;

import com.mangmuganae.eolmago.domain.training.entity.Criteria;
import com.mangmuganae.eolmago.domain.training.entity.Evaluation;
import com.mangmuganae.eolmago.domain.training.entity.Evaluator;
import com.mangmuganae.eolmago.domain.training.entity.HotdealPost;
import com.mangmuganae.eolmago.domain.training.entity.PostComment;
import com.mangmuganae.eolmago.domain.training.repository.CriteriaRepository;
import com.mangmuganae.eolmago.domain.training.repository.EvaluationRepository;
import com.mangmuganae.eolmago.domain.training.repository.EvaluatorRepository;
import com.mangmuganae.eolmago.domain.training.repository.HotdealPostRepository;
import com.mangmuganae.eolmago.domain.training.repository.PostCommentRepository;
import com.mangmuganae.eolmago.infra.lamda.LamdaUtil;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrainingService {

  private static final int MAX_EVALUATION_COUNT = 3;
  private static final int DUPLICATE_SIMILARITY_THRESHOLD = 90;
  private static final double THRESHOLD = 0.7;

  private static final String QUASARZONE_BASE_URL = "https://quasarzone.com";
  private static final String QUASARZONE_LIST_URL = QUASARZONE_BASE_URL + "/bbs/qb_saleinfo";
  private static final int QUASARZONE_TIMEOUT_MS = 10000;

  // collector/http_client.py의 build_session() 고정 헤더를 그대로 따른다.
  // br/zstd는 Jsoup이 못 풀어 깨진 응답이 되므로 제외하고, referer도 보내지 않는다.
  private static final Map<String, String> QUASARZONE_HEADERS = Map.ofEntries(
      Map.entry("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/jxl,image/avif,"
          + "image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
      Map.entry("accept-encoding", "gzip, deflate"),
      Map.entry("accept-language", "en-US,en;q=0.9"),
      Map.entry("cache-control", "no-cache"),
      Map.entry("pragma", "no-cache"),
      Map.entry("priority", "u=0, i"),
      Map.entry("sec-ch-ua", "\"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"156\", \"Google Chrome\";v=\"156\""),
      Map.entry("sec-ch-ua-mobile", "?0"),
      Map.entry("sec-ch-ua-platform", "\"macOS\""),
      Map.entry("sec-fetch-dest", "document"),
      Map.entry("sec-fetch-mode", "navigate"),
      Map.entry("sec-fetch-site", "none"),
      Map.entry("sec-fetch-user", "?1"),
      Map.entry("upgrade-insecure-requests", "1")
  );
  private static final String QUASARZONE_USER_AGENT =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
          + "(KHTML, like Gecko) Chrome/156.0.0.0 Safari/537.36";

  // 브라우저 세션처럼 쿠키(cf_clearance 등)를 요청 간에 들고 다닌다.
  // Turnstile에 막히면 브라우저에서 캡차를 통과한 쿠키 문자열을 QUASARZONE_COOKIE 환경변수로 넣어 쓴다(파이썬과 동일).
  private static final Map<String, String> QUASARZONE_COOKIES = new ConcurrentHashMap<>();

  static {
    String cookie = System.getenv("QUASARZONE_COOKIE");
    if (cookie != null && !cookie.isBlank()) {
      for (String pair : cookie.split(";")) {
        String[] kv = pair.trim().split("=", 2);
        if (kv.length == 2) {
          QUASARZONE_COOKIES.put(kv[0].trim(), kv[1].trim());
        }
      }
    }
  }

  private Connection newQuasarzoneConnection(String url) {
    return Jsoup.connect(url)
        .userAgent(QUASARZONE_USER_AGENT)
        .headers(QUASARZONE_HEADERS)
        .cookies(QUASARZONE_COOKIES)
        .timeout(QUASARZONE_TIMEOUT_MS)
        .maxBodySize(0);
  }

  private Document executeQuasarzoneRequest(String url) throws IOException {
    Connection.Response response = newQuasarzoneConnection(url).execute();
    QUASARZONE_COOKIES.putAll(response.cookies());
    return response.parse();
  }

  private final EvaluatorRepository evaluatorRepository;
  private final HotdealPostRepository hotdealPostRepository;
  private final EvaluationRepository evaluationRepository;
  private final CriteriaRepository criteriaRepository;
  private final PostCommentRepository postCommentRepository;

  private final LamdaUtil lamdaUtil;
  private final ObjectMapper objectMapper;

  public Boolean getModelValue(String text) {

    double confidence = lamdaUtil.getAdConfidence(text);

    if(confidence >= THRESHOLD) {
      return true;
    }

    return false;

  }

  // 목록 행(.v2-list-row.v2-list-row--hotdeal)에서 썸네일/가격/쇼핑몰/조회/댓글/시간까지 그대로 뽑아 중계한다.
  public List<Map<String, Object>> fetchQuasarzoneList(int page) {
    Document doc;
    try {
      doc = executeQuasarzoneRequest(QUASARZONE_LIST_URL + "?page=" + page);
    } catch (IOException e) {
      log.warn("퀘이사존 목록 조회 실패 page={}", page, e);
      return List.of();
    }

    List<Map<String, Object>> items = new ArrayList<>();
    for (Element row : doc.select("div.v2-list-row.v2-list-row--hotdeal")) {
      if (row.hasClass("v2-partner-row")) {
        continue;
      }
      String postId = row.attr("data-qc-id");
      Element titleAnchor = row.selectFirst(".tit .subject-link");
      if (postId.isEmpty() || titleAnchor == null) {
        continue;
      }
      String title = titleAnchor.text().trim();
      if (title.isEmpty()) {
        continue;
      }

      Map<String, Object> item = new HashMap<>();
      item.put("id", postId);
      item.put("title", title);
      item.put("link", QUASARZONE_BASE_URL + "/bbs/qb_saleinfo/views/" + postId);
      item.put("thumbnail", row.hasAttr("data-preview") ? row.attr("data-preview") : null);
      Element badge = row.selectFirst(".v2-badge");
      item.put("category", badge != null ? badge.text().trim() : null);
      item.put("isHot", row.selectFirst(".v2-badge--hot") != null);
      item.put("isDone", row.hasClass("is-done"));
      Element priceEl = row.selectFirst(".v2-list-row__price");
      item.put("price", priceEl != null ? priceEl.text().trim() : null);
      Element shopLogo = row.selectFirst(".v2-shop-logo");
      item.put("shopName", shopLogo != null ? shopLogo.attr("alt").trim() : null);
      Element commentEl = row.selectFirst(".ctn-count");
      item.put("commentCount", commentEl != null ? commentEl.text().trim() : "0");
      Element hitEl = row.selectFirst(".qc-count-hit");
      item.put("viewCount", hitEl != null ? hitEl.text().trim() : null);
      Element timeEl = row.selectFirst(".v2-list-row__time");
      item.put("timeAgo", timeEl != null ? timeEl.text().trim() : null);
      items.add(item);
    }
    return items;
  }

  public Map<String, Object> fetchQuasarzoneDetail(String postId) {
    String url = QUASARZONE_BASE_URL + "/bbs/qb_saleinfo/views/" + postId;
    Map<String, Object> result = new HashMap<>();
    result.put("id", postId);
    result.put("link", url);

    Document doc;
    try {
      doc = executeQuasarzoneRequest(url);
    } catch (HttpStatusException e) {
      result.put("status", e.getStatusCode() == 404 ? "DELETED" : "ERROR");
      return result;
    } catch (IOException e) {
      log.warn("퀘이사존 상세 조회 실패 postId={}", postId, e);
      result.put("status", "ERROR");
      return result;
    }

    for (Element script : doc.select("script[type=application/ld+json]")) {
      JsonNode node;
      try {
        node = objectMapper.readTree(script.data());
      } catch (RuntimeException e) {
        continue;
      }
      JsonNode found = findHeadlineNode(node);
      if (found == null) {
        continue;
      }
      // headline만 있고 text가 없는 글(사진/링크만 있는 정상 글)도 있다 — 성공 조건은 headline 존재 여부뿐이다.
      String title = found.path("headline").asString("").trim();
      if (title.isEmpty()) {
        continue;
      }
      String content = found.hasNonNull("text") ? found.path("text").asString("")
          : found.path("articleBody").asString("");
      result.put("status", "OK");
      result.put("title", Jsoup.parse(title).text());
      result.put("content", Jsoup.parse(content).text());
      return result;
    }

    result.put("status", "BLIND");
    return result;
  }

  private JsonNode findHeadlineNode(JsonNode node) {
    if (node == null || node.isMissingNode()) {
      return null;
    }
    if (node.isArray()) {
      for (JsonNode child : node) {
        JsonNode found = findHeadlineNode(child);
        if (found != null) {
          return found;
        }
      }
      return null;
    }
    if (node.has("headline")) {
      return node;
    }
    if (node.has("@graph")) {
      return findHeadlineNode(node.get("@graph"));
    }
    return null;
  }

  public TrainingService(EvaluatorRepository evaluatorRepository,
      HotdealPostRepository hotdealPostRepository,
      EvaluationRepository evaluationRepository,
      CriteriaRepository criteriaRepository,
      PostCommentRepository postCommentRepository, LamdaUtil lamdaUtil,
      ObjectMapper objectMapper) {
    this.evaluatorRepository = evaluatorRepository;
    this.hotdealPostRepository = hotdealPostRepository;
    this.evaluationRepository = evaluationRepository;
    this.criteriaRepository = criteriaRepository;
    this.postCommentRepository = postCommentRepository;
    this.lamdaUtil = lamdaUtil;
    this.objectMapper = objectMapper;
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
    String safeKeyword = keyword == null ? "" : keyword.trim();
    Long idFilter = safeKeyword.matches("\\d+") ? Long.valueOf(safeKeyword) : null;
    List<HotdealPost> posts = hotdealPostRepository.findByStatusAndKeyword(status, safeKeyword, idFilter);

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

  // 1순위: 평가 0개인 글, 2순위: 내가 평가 안 한 글(평가 개수 적은순), 3순위: 내가 평가한 글(평가 개수 적은순)
  public List<Map<String, Object>> getEvaluationQueue(Evaluator evaluator) {
    List<HotdealPost> posts = hotdealPostRepository.findByStatus(HotdealPost.STATUS_PENDING);

    List<Map<String, Object>> items = new ArrayList<>();
    for (HotdealPost post : posts) {
      List<Evaluation> evaluations = evaluationRepository.findByPostId(post.getId());
      Evaluation mine = evaluations.stream()
          .filter(evaluation -> evaluation.getEvaluatorId().equals(evaluator.getId()))
          .findFirst()
          .orElse(null);

      int evalCount = evaluations.size();
      int priority = evalCount == 0 ? 0 : (mine == null ? 1 : 2);

      Map<String, Object> item = toPostSummary(post);
      item.put("priority", priority);
      item.put("myLabel", mine == null ? null : mine.getLabel());
      item.put("myEvaluationId", mine == null ? null : mine.getId());
      item.put("commentCount", postCommentRepository.countByPostId(post.getId()));
      items.add(item);
    }

    items.sort(Comparator
        .comparingInt((Map<String, Object> item) -> (Integer) item.get("priority"))
        .thenComparingInt(item -> (Integer) item.get("evaluationCount"))
        .thenComparing(item -> (Long) item.get("id")));
    return items;
  }

  public List<Map<String, Object>> listComments(Long postId) {
    List<Map<String, Object>> rows = new ArrayList<>();
    for (PostComment comment : postCommentRepository.findByPostId(postId)) {
      Map<String, Object> row = new HashMap<>();
      row.put("id", comment.getId());
      row.put("evaluatorName", comment.getEvaluatorName());
      row.put("content", comment.getContent());
      row.put("createdAt", comment.getCreatedAt());
      rows.add(row);
    }
    return rows;
  }

  public Map<String, Object> addComment(Long postId, Evaluator evaluator, String content) {
    PostComment saved = postCommentRepository.save(postId, evaluator.getId(), evaluator.getName(), content);
    Map<String, Object> row = new HashMap<>();
    row.put("id", saved.getId());
    row.put("evaluatorName", saved.getEvaluatorName());
    row.put("content", saved.getContent());
    row.put("createdAt", saved.getCreatedAt());
    return row;
  }

  public void deleteComment(Long commentId) {
    postCommentRepository.deleteById(commentId);
  }

  public Map<String, Object> submitEvaluation(Long postId, Evaluator evaluator, int label) {
    int currentCount = evaluationRepository.countByPostId(postId);
    Map<String, Object> result = new HashMap<>();

    if (currentCount >= MAX_EVALUATION_COUNT) {
      result.put("success", false);
      result.put("message", "이미 평가가 3번 모두 완료된 게시글입니다.");
      return result;
    }

    Evaluation saved = evaluationRepository.save(postId, evaluator.getId(), evaluator.getName(), label);
    int newCount = currentCount + 1;

    if (newCount >= MAX_EVALUATION_COUNT) {
      finalizePost(postId);
    }

    result.put("success", true);
    result.put("evaluationId", saved.getId());
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
