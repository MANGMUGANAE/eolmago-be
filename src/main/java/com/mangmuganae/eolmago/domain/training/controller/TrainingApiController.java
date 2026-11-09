package com.mangmuganae.eolmago.domain.training.controller;

import com.mangmuganae.eolmago.domain.training.entity.Evaluator;
import com.mangmuganae.eolmago.domain.training.service.TrainingService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingApiController {

  private static final String SESSION_EVALUATOR_ID = "evaluatorId";
  private static final String SESSION_EVALUATOR_NAME = "evaluatorName";

  private final TrainingService trainingService;

  public record LoginRequest(String name, String pin) {

  }

  public record UploadRequest(List<TrainingService.PostItem> items) {

  }

  public record LabelRequest(Integer label) {

  }

  @PostMapping("/api/login")
  public ResponseDTO<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
    return trainingService.login(request.name(), request.pin())
        .map(evaluator -> {
          session.setAttribute(SESSION_EVALUATOR_ID, evaluator.getId());
          session.setAttribute(SESSION_EVALUATOR_NAME, evaluator.getName());
          Map<String, Object> data = new HashMap<>();
          data.put("id", evaluator.getId());
          data.put("name", evaluator.getName());
          return ResponseDTO.of(data, "로그인 성공");
        })
        .orElseGet(() -> ResponseDTO.of("비밀번호가 일치하지 않습니다."));
  }

  @PostMapping("/api/logout")
  public ResponseDTO<Void> logout(HttpSession session) {
    session.invalidate();
    return ResponseDTO.of("로그아웃 되었습니다.");
  }

  @GetMapping("/api/me")
  public ResponseDTO<Map<String, Object>> me(HttpSession session) {
    Long evaluatorId = (Long) session.getAttribute(SESSION_EVALUATOR_ID);
    if (evaluatorId == null) {
      return ResponseDTO.of("로그인되어 있지 않습니다.");
    }
    String name = (String) session.getAttribute(SESSION_EVALUATOR_NAME);
    Map<String, Object> data = new HashMap<>();
    data.put("id", evaluatorId);
    data.put("name", name);
    return ResponseDTO.of(data, "OK");
  }

  @GetMapping("/api/posts")
  public ResponseDTO<List<Map<String, Object>>> listPosts(
      @RequestParam String status,
      @RequestParam(required = false) String keyword) {
    return ResponseDTO.of(trainingService.listPosts(status, keyword), "조회 성공");
  }

  @PostMapping("/api/posts")
  public ResponseDTO<List<Map<String, Object>>> uploadPosts(
      @RequestBody UploadRequest request, HttpSession session) {
    Evaluator evaluator = currentEvaluator(session);
    return ResponseDTO.of(trainingService.uploadPosts(request.items(), evaluator), "업로드 처리 완료");
  }

  @GetMapping("/api/posts/{id}")
  public ResponseDTO<Map<String, Object>> getPostDetail(@PathVariable Long id) {
    return ResponseDTO.of(trainingService.getPostDetail(id), "조회 성공");
  }

  @PostMapping("/api/posts/{id}/evaluations")
  public ResponseDTO<Map<String, Object>> submitEvaluation(
      @PathVariable Long id, @RequestBody LabelRequest request, HttpSession session) {
    Evaluator evaluator = currentEvaluator(session);
    return ResponseDTO.of(trainingService.submitEvaluation(id, evaluator, request.label()), "평가 제출 완료");
  }

  @PutMapping("/api/evaluations/{id}")
  public ResponseDTO<Map<String, Object>> updateEvaluation(
      @PathVariable Long id, @RequestBody LabelRequest request) {
    return ResponseDTO.of(trainingService.updateEvaluation(id, request.label()), "평가 수정 완료");
  }

  @DeleteMapping("/api/posts/{id}")
  public ResponseDTO<Void> deletePost(@PathVariable Long id) {
    trainingService.deletePost(id);
    return ResponseDTO.of("삭제되었습니다.");
  }

  @DeleteMapping("/api/evaluations/{id}")
  public ResponseDTO<Void> deleteEvaluation(@PathVariable Long id) {
    trainingService.deleteEvaluation(id);
    return ResponseDTO.of("삭제되었습니다.");
  }

  @GetMapping("/api/export")
  public ResponseDTO<String> export() {
    return ResponseDTO.of(trainingService.exportCsv(), "조회 성공");
  }

  public record CriteriaRequest(String content) {

  }

  @GetMapping("/api/criteria")
  public ResponseDTO<String> getCriteria() {
    return ResponseDTO.of(trainingService.getCriteria(), "조회 성공");
  }

  @PutMapping("/api/criteria")
  public ResponseDTO<Void> updateCriteria(@RequestBody CriteriaRequest request) {
    trainingService.updateCriteria(request.content());
    return ResponseDTO.of("수정되었습니다.");
  }

  private Evaluator currentEvaluator(HttpSession session) {
    Long id = (Long) session.getAttribute(SESSION_EVALUATOR_ID);
    String name = (String) session.getAttribute(SESSION_EVALUATOR_NAME);
    return Evaluator.builder().id(id).name(name).build();
  }

}
