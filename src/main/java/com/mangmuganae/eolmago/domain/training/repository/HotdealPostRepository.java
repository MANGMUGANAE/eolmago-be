package com.mangmuganae.eolmago.domain.training.repository;

import com.mangmuganae.eolmago.domain.training.entity.HotdealPost;
import com.mangmuganae.eolmago.domain.training.entity.HotdealPostRowMapper;
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
public class HotdealPostRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<HotdealPost> hotdealPostRowMapper = new HotdealPostRowMapper();

  public HotdealPostRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<HotdealPost> findById(Long id) {
    String sql = "select * from hotdeal_post where id = ?";
    return jdbcTemplate.query(sql, hotdealPostRowMapper, id)
        .stream()
        .findFirst();
  }

  public List<HotdealPost> findAll() {
    String sql = "select * from hotdeal_post";
    return jdbcTemplate.query(sql, hotdealPostRowMapper);
  }

  public List<HotdealPost> findByStatus(String status) {
    String sql = "select * from hotdeal_post where status = ?";
    return jdbcTemplate.query(sql, hotdealPostRowMapper, status);
  }

  public List<HotdealPost> findCommunityFeed() {
    String sql = "select * from hotdeal_post order by created_at desc";
    return jdbcTemplate.query(sql, hotdealPostRowMapper);
  }

  public List<HotdealPost> findByStatusAndKeyword(String status, String keyword, Long idFilter) {
    String sql = "select * from hotdeal_post where status = ? "
        + "and (title like ? or content like ? or id = ?) order by created_at desc";
    String likeKeyword = "%" + keyword + "%";
    return jdbcTemplate.query(sql, hotdealPostRowMapper, status, likeKeyword, likeKeyword, idFilter);
  }

  public List<HotdealPost> findLabeledForExport() {
    String sql = "select * from hotdeal_post where status = 'COMPLETED' "
        + "and final_label in (0, 1) order by id";
    return jdbcTemplate.query(sql, hotdealPostRowMapper);
  }

  public HotdealPost save(String title, String content, String contentHash, String sourceUrl) {
    String sql = "insert into hotdeal_post (title, content, content_hash, source_url, status, created_at) "
        + "values (?, ?, ?, ?, 'PENDING', now())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, title);
      ps.setString(2, content);
      ps.setString(3, contentHash);
      ps.setString(4, sourceUrl);
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();
    return findById(id).orElseThrow();
  }

  public void finalizeLabel(Long id, int finalLabel) {
    String sql = "update hotdeal_post set final_label = ?, status = 'COMPLETED' where id = ?";
    jdbcTemplate.update(sql, finalLabel, id);
  }

  public void revertToPending(Long id) {
    String sql = "update hotdeal_post set final_label = null, status = 'PENDING' where id = ?";
    jdbcTemplate.update(sql, id);
  }

  public void deleteById(Long id) {
    String sql = "delete from hotdeal_post where id = ?";
    jdbcTemplate.update(sql, id);
  }

}
