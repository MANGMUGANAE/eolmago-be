package com.mangmuganae.eolmago.domain.training.repository;

import com.mangmuganae.eolmago.domain.training.entity.Evaluation;
import com.mangmuganae.eolmago.domain.training.entity.EvaluationRowMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluationRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Evaluation> evaluationRowMapper = new EvaluationRowMapper();

  public EvaluationRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<Evaluation> findById(Long id) {
    String sql = "select * from evaluation where id = ?";
    return jdbcTemplate.query(sql, evaluationRowMapper, id)
        .stream()
        .findFirst();
  }

  public List<Evaluation> findByPostId(Long postId) {
    String sql = "select * from evaluation where post_id = ? order by created_at";
    return jdbcTemplate.query(sql, evaluationRowMapper, postId);
  }

  public int countByPostId(Long postId) {
    String sql = "select count(*) from evaluation where post_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
    return count == null ? 0 : count;
  }

  public Evaluation save(Long postId, Long evaluatorId, String evaluatorName, int label) {
    String sql = "insert into evaluation (post_id, evaluator_id, evaluator_name, label, created_at) "
        + "values (?, ?, ?, ?, now())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, postId);
      ps.setLong(2, evaluatorId);
      ps.setString(3, evaluatorName);
      ps.setInt(4, label);
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();
    return findById(id).orElseThrow();
  }

  public void updateLabel(Long evaluationId, int label) {
    String sql = "update evaluation set label = ? where id = ?";
    jdbcTemplate.update(sql, label, evaluationId);
  }

  public void deleteById(Long id) {
    String sql = "delete from evaluation where id = ?";
    jdbcTemplate.update(sql, id);
  }

}
