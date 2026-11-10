package com.mangmuganae.eolmago.domain.training.repository;

import com.mangmuganae.eolmago.domain.training.entity.PostComment;
import com.mangmuganae.eolmago.domain.training.entity.PostCommentRowMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PostCommentRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<PostComment> postCommentRowMapper = new PostCommentRowMapper();

  public PostCommentRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<PostComment> findByPostId(Long postId) {
    String sql = "select * from hotdeal_post_comment where post_id = ? order by created_at";
    return jdbcTemplate.query(sql, postCommentRowMapper, postId);
  }

  public int countByPostId(Long postId) {
    String sql = "select count(*) from hotdeal_post_comment where post_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
    return count == null ? 0 : count;
  }

  public PostComment save(Long postId, Long evaluatorId, String evaluatorName, String content) {
    String sql = "insert into hotdeal_post_comment (post_id, evaluator_id, evaluator_name, content, created_at) "
        + "values (?, ?, ?, ?, now())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, postId);
      ps.setLong(2, evaluatorId);
      ps.setString(3, evaluatorName);
      ps.setString(4, content);
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();
    return findByPostId(postId).stream()
        .filter(comment -> comment.getId().equals(id))
        .findFirst()
        .orElseThrow();
  }

  public void deleteById(Long id) {
    String sql = "delete from hotdeal_post_comment where id = ?";
    jdbcTemplate.update(sql, id);
  }

}
