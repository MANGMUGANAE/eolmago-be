package com.mangmuganae.eolmago.domain.training.repository;

import com.mangmuganae.eolmago.domain.training.entity.Criteria;
import com.mangmuganae.eolmago.domain.training.entity.CriteriaRowMapper;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CriteriaRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Criteria> criteriaRowMapper = new CriteriaRowMapper();

  public CriteriaRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<Criteria> findFirst() {
    String sql = "select * from criteria order by id limit 1";
    return jdbcTemplate.query(sql, criteriaRowMapper)
        .stream()
        .findFirst();
  }

  public void updateContent(Long id, String content) {
    String sql = "update criteria set content = ?, updated_at = now() where id = ?";
    jdbcTemplate.update(sql, content, id);
  }

}
