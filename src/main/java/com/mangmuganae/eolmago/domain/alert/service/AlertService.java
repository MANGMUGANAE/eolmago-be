package com.mangmuganae.eolmago.domain.alert.service;

import com.mangmuganae.eolmago.domain.alert.dto.AlertDto;
import com.mangmuganae.eolmago.domain.alert.dto.AlertResponseDto;
import com.mangmuganae.eolmago.domain.alert.dto.CategoryAlertDto;
import com.mangmuganae.eolmago.domain.alert.dto.KeywordAlertDto;
import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import com.mangmuganae.eolmago.domain.alert.exception.AlertNotFoundException;
import com.mangmuganae.eolmago.domain.alert.exception.DuplicatedAlertException;
import com.mangmuganae.eolmago.domain.alert.repository.AlertRepository;
import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.category.exception.CategoryNotFoundException;
import com.mangmuganae.eolmago.domain.category.repository.CategoryRepository;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertService {

  private final AlertRepository alertRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public void registerAlert(AlertDto alertDto, String uuid) {

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    Alert alert;

    if (alertDto.getKeyword() == null || alertDto.getKeyword().isBlank()) {

      Category category = categoryRepository.findById(alertDto.getCategoryId()).orElseThrow(
          () -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND)
      );

      if (alertRepository.existsByUserAndCategory(user, category)) {
        throw new DuplicatedAlertException(ErrorCode.DUPLICATED_ALERT);
      }

      alert = Alert.of(null, user, category);
    } else {
      String keyword = alertDto.getKeyword().trim();

      if (alertRepository.existsByUserAndKeyword(user, keyword)) {
        throw new DuplicatedAlertException(ErrorCode.DUPLICATED_ALERT);
      }

      alert = Alert.of(keyword, user, null);
    }

    alertRepository.save(alert);
  }

  @Transactional(readOnly = true)
  public AlertResponseDto findMyAlerts(String uuid) {

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    List<Alert> alerts = alertRepository.findAllByUser(user);

    Map<Long, Long> alertIdByCategoryId = alerts.stream()
        .filter(alert -> alert.getCategory() != null)
        .collect(Collectors.toMap(
            alert -> alert.getCategory().getId(),
            Alert::getId,
            (left, right) -> left
        ));

    List<CategoryAlertDto> categories = categoryRepository.findAll().stream()
        .map(category -> CategoryAlertDto.of(
            alertIdByCategoryId.get(category.getId()),
            category.getId(),
            category.getCategoryName()
        ))
        .toList();

    List<KeywordAlertDto> keywords = alerts.stream()
        .filter(alert -> alert.getKeyword() != null && !alert.getKeyword().isBlank())
        .map(alert -> KeywordAlertDto.of(alert.getId(), alert.getKeyword()))
        .toList();

    return AlertResponseDto.of(categories, keywords);
  }

  @Transactional
  public void deleteAlert(Long alertId, String uuid) {

    Alert alert = alertRepository.findById(alertId).orElseThrow(
        () -> new AlertNotFoundException(ErrorCode.ALERT_NOT_FOUND)
    );

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    if (alertRepository.findByAlertAndUser(alert, user).isEmpty()) {
      throw new AlertNotFoundException(ErrorCode.ALERT_NOT_FOUND);
    }

    alertRepository.delete(alert);
  }
}
