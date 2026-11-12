package com.mangmuganae.eolmago.domain.training.repository;

import com.mangmuganae.eolmago.domain.training.entity.Evaluator;
import com.mangmuganae.eolmago.domain.training.entity.EvaluatorRowMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluatorRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Evaluator> evaluatorRowMapper = new EvaluatorRowMapper();

  public EvaluatorRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<Evaluator> findByName(String name) {
    String sql = "select * from evaluator where name = ?";
    return jdbcTemplate.query(sql, evaluatorRowMapper, name)
        .stream()
        .findFirst();
  }

  public Optional<Evaluator> findById(Long id) {
    String sql = "select * from evaluator where id = ?";
    return jdbcTemplate.query(sql, evaluatorRowMapper, id)
        .stream()
        .findFirst();
  }

  public void updatePin(Long id, String pin) {
    String sql = "update evaluator set pin = ? where id = ?";
    jdbcTemplate.update(sql, pin, id);
  }

  public Evaluator save(String name, String pin) {
    String sql = "insert into evaluator (name, pin, created_at) values (?, ?, now())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);
      ps.setString(2, pin);
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();
    return findById(id).orElseThrow();
  }

}
